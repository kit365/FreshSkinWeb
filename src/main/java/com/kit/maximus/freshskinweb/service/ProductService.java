package com.kit.maximus.freshskinweb.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.ProductBrandRepository;
import com.kit.maximus.freshskinweb.repository.ProductCategoryRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import com.kit.maximus.freshskinweb.utils.SkinType;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductService implements BaseService<ProductResponseDTO, CreateProductRequest, UpdateProductRequest, Long> {

    ProductRepository productRepository;

    ProductMapper productMapper;

    ProductCategoryRepository productCategoryRepository;

    ProductBrandRepository productBrandRepository;

    SkinTypeRepository skinTypeRepository;

    Cloudinary cloudinary;


    private String getNameFile(String slug, int count) {
        String fileName;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        if (count <= 0) {
            return slug + "_" + timestamp;
        }
        return slug + "_" + timestamp + "_" + (count + 1);

    }

    private String uploadImageFromFile(MultipartFile file, String slug, int count) throws IOException {

        String fileName = getNameFile(slug, count);


        Map params = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", false,
                "folder", "product",
                "public_id", fileName
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    @Override
    public boolean add(CreateProductRequest request) {
        List<ProductCategoryEntity> productCategoryEntity = productCategoryRepository.findAllById(request.getCategoryId());
        ProductBrandEntity productBrandEntity = productBrandRepository.findById(request.getBrandId()).orElse(null);
        ProductEntity productEntity = productMapper.productToProductEntity(request);


        if (request.getThumbnail() != null) {
            int count = 0;
            List<String> thumbnails = new ArrayList<>();

            for (MultipartFile file : request.getThumbnail()) {
                try {
                    String slg = getSlug(request.getTitle());
                    String img = uploadImageFromFile(file, slg, count++);
                    thumbnails.add(img);
                } catch (IOException e) {
                    log.error("Upload thumbnail error", e);
                }
            }
            productEntity.setThumbnail(thumbnails);
        }


        if (productCategoryEntity != null) {
            productEntity.setCategory(productCategoryEntity);
        }

        if (productBrandEntity != null) {
            productEntity.setBrand(productBrandEntity);
        }

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = productRepository.findAll().size();
            productEntity.setPosition(size + 1);
        }

        productEntity.setSlug(getSlug(request.getTitle()));

        request.getVariants().forEach(productEntity::createProductVariant);

        List<SkinTypeEntity> listSkinType = skinTypeRepository.findAllById(request.getSkinTypes());
        productEntity.setSkinTypes(listSkinType);

        productRepository.save(productEntity);

        return true;
    }


    //noted: thêm set thumb vao entity sau khi update
    @Override
    public ProductResponseDTO update(Long id, UpdateProductRequest request) {
        if (StringUtils.hasLength(request.getStatus())) {
            request.setStatus(request.getStatus().toUpperCase());
            getStatus(request.getStatus());
        }

        ProductEntity listProduct = getProductEntityById(id);




        if (request.getThumbnail() != null) {
            listProduct.getThumbnail().forEach(thumbnail -> {
                try {
                    deleteImageFromCloudinary(thumbnail);
                } catch (IOException e) {
                    log.error("Delete thumbnail error", e);
                    throw new RuntimeException(e);
                }
            });
            int count = 0;
            List<String> newThumbnails = new ArrayList<>();
            for (MultipartFile file : request.getThumbnail()) {
                try {
                    String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                    newThumbnails.add(url);
                } catch (IOException e) {
                    log.error("Upload thumbnail error", e);
                    throw new RuntimeException(e);
                }
            }
            listProduct.setThumbnail(newThumbnails);
        }
//        //Upload hinh => Xoa hinh cu, add hinh moi
//        if (request.getThumbnail() != null) {
//
//            List<String> oldThumbnails = listProduct.getThumbnail();
//            List<MultipartFile> newThumbnails = request.getThumbnail();
//            List<String> updatedThumbnails = new ArrayList<>();

//           String thumbnailBytes =  IOUtils.readFileToString( request.getThumbnail());


//            //Xóa hình khi không có trong request
//            for (String oldThumbnail : oldThumbnails) {
//                if (!newThumbnails.contains(oldThumbnail)) {
//                    try {
//                        long start = System.currentTimeMillis();
//                        deleteImageFromCloudinary(oldThumbnail);
//                        long end = System.currentTimeMillis();
//                        log.info("Thời gian xóa ảnh {}: {}ms", oldThumbnail, (end - start));
//                    } catch (IOException e) {
//                        log.error("delete image error: {}", oldThumbnail);
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    updatedThumbnails.add(oldThumbnail);
//                }
//            }
//
//            //Cập nhập lại hình mới
//            for (MultipartFile newThumbnail : newThumbnails) {
//                if (!oldThumbnails.contains(newThumbnail)) {
//                    try {
//                        String img = uploadImage(newThumbnail);
//                        updatedThumbnails.add(img);
//                    } catch (IOException e) {
//                        log.error("delete image error: {}", newThumbnail);
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//            listProduct.setThumbnail(updatedThumbnails);
//        }

        if (StringUtils.hasLength(request.getTitle())) {
            listProduct.setSlug(getSlug(request.getTitle()));
        }


        //BO SUNG BAN LOI KHONG TIM THAY ID DANH MUC SAN PHAM
        if (request.getCategoryId() != null) {
            List<ProductCategoryEntity> productCategoryEntity = productCategoryRepository.findAllById(request.getCategoryId());
            listProduct.setCategory(productCategoryEntity);
        }

        if (request.getBrandId() > 0) {
            ProductBrandEntity productBrandEntity = productBrandRepository.findById(request.getBrandId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_BRAND_NOT_FOUND));
            listProduct.setBrand(productBrandEntity);
        }

        if(request.getSkinTypeId() != null) {
            List<SkinTypeEntity> skinTypeEntities = skinTypeRepository.findAllById(request.getSkinTypeId());
            listProduct.setSkinTypes(skinTypeEntities);
        }


        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            Map<Integer, ProductVariantEntity> requestList = listProductVariantToMap(request.getVariants());
            Map<Integer, ProductVariantEntity> currentList = listProductVariantToMap(listProduct.getVariants());
            //duyệt vào cập nhật danh sách có trong request
            for (ProductVariantEntity listUpdate : requestList.values()) {
                if (currentList.containsKey(listUpdate.getVolume())) {
                    ProductVariantEntity productVariantEntity = currentList.get(listUpdate.getVolume());
                    //tương lai sẽ vứt loi
                    if (productVariantEntity.getPrice() < 0) productVariantEntity.setPrice(0);
                    productVariantEntity.setPrice(listUpdate.getPrice());
                } else {
                    listProduct.createProductVariant(listUpdate);
                }
            }

            for (ProductVariantEntity listUpdate : currentList.values()) {
                if (!requestList.containsKey(listUpdate.getVolume())) {
                    listProduct.removeProductVariant(listUpdate);
                }
            }
        }


        productMapper.updateProduct(listProduct, request);
        return productMapper.productToProductResponseDTO(productRepository.save(listProduct));
    }

    private Map<Integer, ProductVariantEntity> listProductVariantToMap(List<ProductVariantEntity> productEntity) {
        Map<Integer, ProductVariantEntity> volumeMap = new HashMap<>();
        for (ProductVariantEntity productVariantEntity : productEntity) {
            volumeMap.put(productVariantEntity.getVolume(), productVariantEntity);
        }
        return volumeMap;
    }

    //thay doi thanh String de quan lý message
    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductEntity> productEntities = productRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productRepository.saveAll(productEntities);
            return "Cập nhật trạng thái sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productRepository.saveAll(productEntities);
            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productRepository.saveAll(productEntities);
            return "Phục hồi thành công";
        }
        return "Cập nhật thất bại";
    }


    /*
       Xóa(cứng) 1 sản phẩm
       input: long id
       output: boolean
     */

    @Override
    public boolean delete(Long id) {
        ProductEntity productEntity = getProductEntityById(id);

        if (productEntity.getThumbnail() != null) {
            for (String deleteThumbnails : productEntity.getThumbnail()) {
                try {
                    deleteImageFromCloudinary(deleteThumbnails);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        log.info("Delete: {}", id);
        productRepository.delete(productEntity);
        return true;
    }

    /*
     Xóa(cứng) nhiều sản phẩm
     input: List<long> id
     output: boolean
   */
    @Override
    public boolean delete(List<Long> longs) {
        List<ProductEntity> productEntities = productRepository.findAllById(longs);

        productEntities.forEach(productEntity -> {
            if (productEntity.getThumbnail() != null) {
                for (String deleteThumbnails : productEntity.getThumbnail()) {
                    try {
                        deleteImageFromCloudinary(deleteThumbnails);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });

        productRepository.deleteAll(productEntities);
        return true;
    }


    /*
     Xóa(mềm) 1 sản phẩm
     input: long id
     output: boolean
   */
    @Override
    public boolean deleteTemporarily(Long id) {
        ProductEntity productEntity = getProductEntityById(id);

        log.info("Delete temporarily : {}", id);
        productEntity.setDeleted(true);
        productRepository.save(productEntity);
        return true;
    }


    /*
     Phục hồi: 1 sản phẩm
     - Khôi phục trạng thái sản phẩm(ACTIVE) và thay đô DELETE(False)
     input: long id
     output: boolean
   */
    @Override
    public boolean restore(Long id) {
        ProductEntity productEntity = getProductEntityById(id);

        productEntity.setDeleted(false);
        productRepository.save(productEntity);

        return true;
    }

    @Override
    public ProductResponseDTO showDetail(Long id) {
        return productMapper.productToProductResponseDTO(getProductEntityById(id));
    }


    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);

        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ProductEntity> productEntityPage;


        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                productEntityPage = productRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                productEntityPage = productRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productEntityPage = productRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                productEntityPage = productRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }


        Page<ProductResponseDTO> list = productEntityPage.map(productMapper::productToProductResponseDTO);
//        if (!list.hasContent()) {
//            return null;
//        }

        map.put("products", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ProductEntity> productEntityPage;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                productEntityPage = productRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, true, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                productEntityPage = productRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, true);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productEntityPage = productRepository.findAllByDeleted(true, pageable);
            } else {
                Status statusEnum = getStatus(status);
                productEntityPage = productRepository.findAllByStatusAndDeleted(statusEnum, true, pageable);
            }
        }

        Page<ProductResponseDTO> list = productEntityPage.map(productMapper::productToProductResponseDTO);

//        if (!list.hasContent()) {
//            return null;
//        }

        map.put("products", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }


    //-------------------------------------------------------------------------------------------------------------
    //tra ve ProductEntity, Neu Id null -> nem loi
    private ProductEntity getProductEntityById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }


    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }


    private SkinType getSkinType(String skintype) {
        try {
            return SkinType.valueOf(skintype.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid skin type provided: '{}'", skintype);
            throw new AppException(ErrorCode.SKINTYPE_INVALID);
        }
    }

    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }


    private void deleteImageFromCloudinary(String imageUrl) throws IOException {
        if (imageUrl != null) {
            String publicId = extractPublicId(imageUrl);
            //xóa vĩnh viễn khỏi cloud
            Map options = ObjectUtils.asMap("invalidate", true);
            cloudinary.uploader().destroy(publicId, options);
        }
    }


    //lấy hình từ ID
    private String extractPublicId(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf(".")); // Lấy ID ảnh từ URL
    }

}
