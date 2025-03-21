package com.kit.maximus.freshskinweb.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.product_brand.CreateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.request.product_brand.UpdateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductBrandResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductBrandEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductBrandMapper;
import com.kit.maximus.freshskinweb.repository.ProductBrandRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductBrandService implements BaseService<ProductBrandResponse, CreateProductBrandRequest, UpdateProductBrandRequest, Long> {

    ProductBrandRepository productBrandRepository;

    ProductBrandMapper productBrandMapper;

    Cloudinary cloudinary;

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands","fullBrands"}, allEntries = true)
    @Override
    public boolean add(CreateProductBrandRequest request) {
        log.info("Request JSON: {}", request);

        ProductBrandEntity productBrandEntity = productBrandMapper.productBrandToProductBrandEntity(request);

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = productBrandRepository.findAll().size();
            productBrandEntity.setPosition(size + 1);
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            List<String> images = new ArrayList<>();
            int count = 0;
            for (MultipartFile createImg : request.getImage()) {
                try {
                    String url = uploadImageFromFile(createImg, getSlug(request.getTitle()), count++);
                    images.add(url);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            productBrandEntity.setImage(images);
        }

        productBrandEntity.setSlug(getSlug(request.getTitle()));
        productBrandRepository.save(productBrandEntity);
        return true;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "fullBrands")
    public List<ProductBrandResponse> getAll() {
        List<ProductBrandResponse> list =  productBrandMapper.toProductBrandsResponseDTO(productBrandRepository.findAll());

        list.forEach(productBrandResponse -> {
            productBrandResponse.setStatus(null);
            productBrandResponse.setCreatedAt(null);
            productBrandResponse.setUpdatedAt(null);
            productBrandResponse.setDescription(null);
            productBrandResponse.setImage(null);
            productBrandResponse.setImage(null);
            productBrandResponse.setPosition(null);
            productBrandResponse.setFeatured(null);
        });

        return list;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "top10ProductBrands")
    public List<ProductBrandResponse> getTop10() {
        List<ProductBrandResponse> list =  productBrandMapper.toProductBrandsResponseDTO(productBrandRepository.findTop10ByStatusAndDeleted(Status.ACTIVE,false));

        list.forEach(productBrandResponse -> {
            productBrandResponse.setStatus(null);
            productBrandResponse.setCreatedAt(null);
            productBrandResponse.setUpdatedAt(null);
            productBrandResponse.setDescription(null);
            productBrandResponse.setImage(null);
            productBrandResponse.setImage(null);
            productBrandResponse.setPosition(null);
            productBrandResponse.setFeatured(null);
        });

        return list;
    }

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands", "fullBrands"}, allEntries = true)
    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductBrandEntity> productBrandEntities = productBrandRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productBrandEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productBrandRepository.saveAll(productBrandEntities);
            return "Cập nhật trạng thái thương hiệu sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productBrandEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productBrandRepository.saveAll(productBrandEntities);
            return "Xóa mềm thương hiệu sản phẩm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productBrandEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productBrandRepository.saveAll(productBrandEntities);
            return "Phục hồi thương hiệu sản phẩm thành công";
        }
        return "Cập nhật thương hiệu sản phẩm thất bại";
    }

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands", "fullBrands"}, allEntries = true)
    @Override
    public ProductBrandResponse update(Long id, UpdateProductBrandRequest request) {
        ProductBrandEntity brandEntity = getBrandById(id);


        if (StringUtils.hasLength(request.getStatus())) {
            brandEntity.setStatus(getStatus(request.getStatus()));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            brandEntity.setSlug(getSlug(request.getTitle()));
        }

        if (request.getImage() != null) {
            brandEntity.getImage().forEach(thumbnail -> {
                try {
                    deleteImageFromCloudinary(thumbnail);
                } catch (IOException e) {
                    log.error("Delete thumbnail error", e);
                    throw new RuntimeException(e);
                }
            });
            int count = 0;
            List<String> newThumbnails = new ArrayList<>();
            for (MultipartFile file : request.getImage()) {
                try {
                    String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                    newThumbnails.add(url);
                } catch (IOException e) {
                    log.error("Upload thumbnail error", e);
                    throw new RuntimeException(e);
                }
            }
            brandEntity.setImage(newThumbnails);
        }

        productBrandMapper.updateProductBrand(brandEntity, request);
        return productBrandMapper.productBrandToProductBrandResponseDTO(productBrandRepository.save(brandEntity));
    }

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands", "fullBrands"}, allEntries = true)
    @Override
    public boolean delete(Long id) {
        ProductBrandEntity brandEntity = getBrandById(id);

        if (brandEntity.getImage() != null && !brandEntity.getImage().isEmpty()) {
            for (String s : brandEntity.getImage()) {
                try {
                    deleteImageFromCloudinary(s);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        log.info("Delete: {}", id);
        productBrandRepository.delete(brandEntity);
        return true;
    }

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands", "fullBrands"}, allEntries = true)
    @Override
    public boolean delete(List<Long> id) {
        List<ProductBrandEntity> list = productBrandRepository.findAllById(id);


        for (ProductBrandEntity productBrandEntity : list) {
            if (productBrandEntity.getImage() != null && !productBrandEntity.getImage().isEmpty()) {
                for (String s : productBrandEntity.getImage()) {
                    try {
                        deleteImageFromCloudinary(s);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }

        }
        productBrandRepository.deleteAll(list);
        return true;
    }

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands", "fullBrands"}, allEntries = true)
    @Override
    public boolean deleteTemporarily(Long id) {
        ProductBrandEntity brandEntity = getBrandById(id);
        brandEntity.setDeleted(true);

        List<ProductEntity> products = brandEntity.getProducts();
        for (ProductEntity productEntity : products) {
            productEntity.setStatus(Status.INACTIVE);
        }
        productBrandRepository.save(brandEntity);

        return true;
    }

    @CacheEvict(value = {"top10ProductBrands", "allProductBrands", "trashProductBrands", "fullBrands"}, allEntries = true)
    @Override
    public boolean restore(Long id) {
        ProductBrandEntity brandEntity = getBrandById(id);
        brandEntity.setDeleted(false);

        List<ProductEntity> products = brandEntity.getProducts();
        for (ProductEntity productEntity : products) {
            productEntity.setStatus(Status.ACTIVE);
        }
        productBrandRepository.save(brandEntity);
        return true;
    }

    @Override
    public ProductBrandResponse showDetail(Long id) {
        ProductBrandEntity brandEntity = getBrandById(id);
        ProductBrandResponse productBrandResponse = productBrandMapper.productBrandToProductBrandResponseDTO(brandEntity);
        productBrandResponse.setProductIDs(getProductIds(brandEntity));
        return productBrandResponse;
    }

    private List<Long> getProductIds(ProductBrandEntity productBrandEntities) {
        List<Long> productIds = new ArrayList<>();
        productBrandEntities.getProducts().forEach(productEntity -> {
            productIds.add(productEntity.getId());
        });
        return productIds;
    }

    @Cacheable(value = "allProductBrands", key = "#page + '-' + #size + '-' + #sortKey + '-' + #sortDirection + '-' + #status + '-' + #keyword")
    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ProductBrandEntity> productBrandEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                productBrandEntities = productBrandRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                productBrandEntities = productBrandRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productBrandEntities = productBrandRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                productBrandEntities = productBrandRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<ProductBrandResponse> list = productBrandEntities.map(productBrandMapper::productBrandToProductBrandResponseDTO);

//        if (!list.hasContent()) {
//            return null;
//        }

        map.put("brand", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    @Cacheable(value = "trashProductBrands", key = "#page + '-' + #size + '-' + #sortKey + '-' + #sortDirection + '-' + #status + '-' + #keyword")
    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ProductBrandEntity> productBrandEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                productBrandEntities = productBrandRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, true, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                productBrandEntities = productBrandRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, true);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productBrandEntities = productBrandRepository.findAllByDeleted(true, pageable);
            } else {
                Status statusEnum = getStatus(status);
                productBrandEntities = productBrandRepository.findAllByStatusAndDeleted(statusEnum, true, pageable);
            }
        }

        Page<ProductBrandResponse> list = productBrandEntities.map(productBrandMapper::productBrandToProductBrandResponseDTO);

//        if (!list.hasContent()) {
//            return null;
//        }

        map.put("brand", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }


    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }

    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    //tra ve ProductEntity, Neu Id null -> nem loi
    private ProductBrandEntity getBrandById(Long id) {
        return productBrandRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_BRAND_NOT_FOUND));
    }

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
                "folder", "product-brand",
                "public_id", fileName
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    private void deleteImageFromCloudinary(String imageUrl) throws IOException {
        if (imageUrl != null) {
            Map options = ObjectUtils.asMap("invalidate", true);
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }


    //lấy hình từ ID
    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        String publicId = temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
        System.out.println(publicId);
        return  publicId;
    }

}

