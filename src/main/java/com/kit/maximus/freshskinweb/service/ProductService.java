package com.kit.maximus.freshskinweb.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.ProductBrandRepository;
import com.kit.maximus.freshskinweb.repository.ProductCategoryRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import com.kit.maximus.freshskinweb.repository.search.ProductSearchRepository;
import com.kit.maximus.freshskinweb.utils.SkinType;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kit.maximus.freshskinweb.specification.ProductSpecification.*;


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

    ProductSearchRepository productSearchRepository;


    // Là annotation của JPA để inject EntityManager instance
    // Quản lý lifecycle của EntityManager
    // Đảm bảo thread-safe khi nhiều request đồng thời truy cập
    @PersistenceContext
    EntityManager entityManager;
    //EntityManager được sử dụng để:
    //+ Thực hiện các thao tác CRUD với database
    //+ Quản lý các entity và lifecycle của chúng
    //+ Thực thi native SQL queries
    //+ Cache các entity

    @Lazy
    ReviewService reviewService; //giảm tỉ lệ bị vòng lap

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


        if (productCategoryEntity.isEmpty()) {
            productEntity.setCategory(productCategoryEntity);
        }

        if (productBrandEntity != null) {
            productEntity.setBrand(productBrandEntity);
        }

//        if(discountEntity != null){
//            productEntity.setDiscountEntity(discountEntity);
//        }

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = productRepository.findAll().size();
            productEntity.setPosition(size + 1);
        }

        productEntity.setSlug(getSlug(request.getTitle()));

        request.getVariants().forEach(productEntity::createProductVariant);

        List<SkinTypeEntity> listSkinType = skinTypeRepository.findAllById(request.getSkinTypes());
        productEntity.setSkinTypes(listSkinType);

        productSearchRepository.indexProduct(mapProductIndexResponsesDTO(productRepository.save(productEntity)));
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

        if (request.getSkinTypeId() != null) {
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
                    if (productVariantEntity.getPrice().compareTo(BigDecimal.ZERO) < 0) productVariantEntity.setPrice(BigDecimal.ZERO);
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

        ProductResponseDTO response = mapProductIndexResponsesDTO(productRepository.save(listProduct));

        // Chạy update OpenSearch trên một thread riêng
        CompletableFuture.runAsync(() -> {
            productSearchRepository.updateProduct(response);
        });

        return response;
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
            List<ProductResponseDTO> productResponseDTO = mapProductIndexResponsesDTO(productRepository.saveAll(productEntities));
            productResponseDTO.forEach(productSearchRepository::updateProduct);

            return "Cập nhật trạng thái sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(true));
            List<ProductResponseDTO> productResponseDTO = mapProductIndexResponsesDTO(productRepository.saveAll(productEntities));
            productResponseDTO.forEach(productSearchRepository::updateProduct);
            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(false));
            List<ProductResponseDTO> productResponseDTO = mapProductIndexResponsesDTO(productRepository.saveAll(productEntities));
            productResponseDTO.forEach(productSearchRepository::updateProduct);
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
        productSearchRepository.deleteProduct(id);
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
            productSearchRepository.deleteProduct(productEntity.getId());
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
        ProductEntity productEntity = getProductEntityById(id);
        ProductResponseDTO productResponseDTO = productMapper.productToProductResponseDTO(productEntity);

        productResponseDTO.setBrand(getProductBrandResponse(productEntity));
        productResponseDTO.setCategory(getProductCategoryResponses(productEntity));
        productResponseDTO.setSkinTypes(getSkinTypeResponses(productEntity));

        if (productResponseDTO.getVariants() != null) {
            List<ProductVariantResponse> productVariantResponses = new ArrayList<>();
            productEntity.getVariants().forEach(productVariantEntity -> {
                ProductVariantResponse newVariant = new ProductVariantResponse();
                newVariant.setId(productVariantEntity.getId());
                newVariant.setUnit(productVariantEntity.getUnit());
                newVariant.setVolume(productVariantEntity.getVolume());
                newVariant.setPrice(productVariantEntity.getPrice());
                productVariantResponses.add(newVariant);
            });
            productResponseDTO.setVariants(productVariantResponses);
        }
        if (productEntity.getReviews() != null) {
            productEntity.getReviews().forEach(productReviewEntity -> {
                System.out.println(productReviewEntity.getReviewId());
            });
        }
        return productResponseDTO;
    }

    //Ham nay de tu map ProductBrand
    private ProductBrandResponse getProductBrandResponse(ProductEntity productEntity) {
        ProductBrandResponse productBrandResponse = new ProductBrandResponse();
        productBrandResponse.setId(productEntity.getBrand().getId());
        productBrandResponse.setTitle(productEntity.getBrand().getTitle());
        productBrandResponse.setDescription(productEntity.getBrand().getDescription());
        productBrandResponse.setImage(productEntity.getBrand().getImage());
        productBrandResponse.setSlug(productEntity.getBrand().getSlug());
        return productBrandResponse;
    }

    //Ham nay de tu map ProductSkinType
    private List<SkinTypeResponse> getSkinTypeResponses(ProductEntity productEntity) {
        List<SkinTypeResponse> skinTypeResponses = new ArrayList<>();

        for (SkinTypeEntity skinTypeEntity : productEntity.getSkinTypes()) {
            SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
            skinTypeResponse.setId(skinTypeEntity.getId());
            skinTypeResponse.setType(skinTypeEntity.getType());
            skinTypeResponse.setDescription(skinTypeEntity.getDescription());
            skinTypeResponses.add(skinTypeResponse);
        }
        return skinTypeResponses;
    }

    //Ham nay de tu map ProductCategory
    private List<ProductCategoryResponse> getProductCategoryResponses(ProductEntity productEntity) {
        List<ProductCategoryResponse> productCategoryResponses = new ArrayList<>();

        for (ProductCategoryEntity productCategoryEntity : productEntity.getCategory()) {
            ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();
            productCategoryResponse.setId(productCategoryEntity.getId());
            productCategoryResponse.setTitle(productCategoryEntity.getTitle());
            productCategoryResponse.setDescription(productCategoryEntity.getDescription());
            productCategoryResponse.setImage(productCategoryEntity.getImage());
            productCategoryResponse.setSlug(productCategoryEntity.getSlug());
            productCategoryResponses.add(productCategoryResponse);
        }
        return productCategoryResponses;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        // Khởi tạo Specification với điều kiện mặc định là không phải đã xóa
        Specification<ProductEntity> specification = Specification.where(isNotDeleted());

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            specification = specification.and(filterByKeyword(keyword));
        }

        // Lọc theo status
        if (status != null && !status.trim().isEmpty()) {
            specification = specification.and(filterByStatus(getStatus(status)));
        }

        // Lọc theo sortKey (price hoặc position)
        if (sortKey.equalsIgnoreCase("position")) {
            specification = specification.and(sortByPosition(getSortDirection(sortDirection)));
        } else if (sortKey.equalsIgnoreCase("price")) {
            specification = specification.and(sortByPrice(getSortDirection(sortDirection)));
        } else if (sortKey.equalsIgnoreCase("title")) {
            specification = specification.and(sortByTitle(getSortDirection(sortDirection)));
        }

        // Tính toán số trang
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size);

        // Thực hiện truy vấn với Specification và Pageable
        Page<ProductEntity> productEntityPage = productRepository.findAll(specification, pageable);

        // Map kết quả trả về thành DTO
        Page<ProductResponseDTO> list = productEntityPage.map(productMapper::productToProductResponseDTO);


        list.forEach(productResponseDTO -> {
            // Tìm ProductEntity tương ứng với DTO hiện tại
            productEntityPage.getContent().stream()
                    .filter(productEntity -> productEntity.getId().equals(productResponseDTO.getId()))
                    .findFirst()
                    .ifPresent(productEntity -> {
                        if (productEntity.getVariants() != null) {
                            List<ProductVariantResponse> productVariantResponses = productEntity.getVariants().stream()
                                    .map(variant -> {
                                        ProductVariantResponse response = new ProductVariantResponse();
                                        response.setId(variant.getId());
                                        response.setUnit(variant.getUnit());
                                        response.setVolume(variant.getVolume());
                                        response.setPrice(variant.getPrice());
                                        return response;
                                    })
                                    .collect(Collectors.toList());

                            productResponseDTO.setVariants(productVariantResponses);
                        }
                    });
        });


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

        // Khởi tạo Specification với điều kiện mặc định là không phải đã xóa
        Specification<ProductEntity> specification = Specification.where(isDeleted());

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            specification = specification.and(filterByKeyword(keyword));
        }

        // Lọc theo status
        if (status != null && !status.trim().isEmpty()) {
            specification = specification.and(filterByStatus(getStatus(status)));
        }

        // Lọc theo sortKey (price hoặc position)
        if (sortKey.equalsIgnoreCase("position")) {
            specification = specification.and(sortByPosition(getSortDirection(sortDirection)));
        } else if (sortKey.equalsIgnoreCase("price")) {
            specification = specification.and(sortByPrice(getSortDirection(sortDirection)));
        }

        // Tính toán số trang
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size);

        // Thực hiện truy vấn với Specification và Pageable
        Page<ProductEntity> productEntityPage = productRepository.findAll(specification, pageable);

        // Map kết quả trả về thành DTO
        Page<ProductResponseDTO> list = productEntityPage.map(productMapper::productToProductResponseDTO);

        list.forEach(productResponseDTO -> {
            // Tìm ProductEntity tương ứng với DTO hiện tại
            productEntityPage.getContent().stream()
                    .filter(productEntity -> productEntity.getId().equals(productResponseDTO.getId()))
                    .findFirst()
                    .ifPresent(productEntity -> {
                        if (productEntity.getVariants() != null) {
                            List<ProductVariantResponse> productVariantResponses = productEntity.getVariants().stream()
                                    .map(variant -> {
                                        ProductVariantResponse response = new ProductVariantResponse();
                                        response.setId(variant.getId());
                                        response.setUnit(variant.getUnit());
                                        response.setVolume(variant.getVolume());
                                        response.setPrice(variant.getPrice());
                                        return response;
                                    })
                                    .collect(Collectors.toList());

                            productResponseDTO.setVariants(productVariantResponses);
                        }
                    });
        });


        // Đóng gói kết quả vào map
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
            Map options = ObjectUtils.asMap("invalidate", true);
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }


    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        String publicId = temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
        System.out.println(publicId);
        return publicId;
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
                "folder", "product",
                "public_id", fileName
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

/*
    Trang home
     */


//    //## 7 sản phẩm có lượt mua cao nhất
//    public List<ProductResponseDTO> findTop7FlashSale() {
//        List<ProductEntity> list = productRepository.findTop7ByStatusAndDeleted(Status.ACTIVE, false, Sort.by(Sort.Direction.DESC, "discountPercent"));
//
//        List<ProductResponseDTO> top7BestSellers = new ArrayList<>();
//
//        if (list != null && !list.isEmpty()) {
//            list.forEach(productEntity -> {
//                ProductResponseDTO productResponseDTO = new ProductResponseDTO();
//                productResponseDTO.setId(productEntity.getId());
//                productResponseDTO.setSlug(productEntity.getSlug());
//                productResponseDTO.setTitle(productEntity.getTitle());
//                productResponseDTO.setThumbnail(productEntity.getThumbnail());
//                productResponseDTO.setDiscountPercent(productEntity.getDiscountPercent());
//
//                //Map với thương hiệu
//                if (productEntity.getBrand() != null) {
//                    ProductBrandResponse productBrandResponse = new ProductBrandResponse();
//                    productBrandResponse.setTitle(productEntity.getBrand().getTitle());
//                    productResponseDTO.setBrand(productBrandResponse);
//                }
//
//                //Lấy giá của product
//                if (productEntity.getVariants() != null) {
//                    List<ProductVariantResponse> productVariantResponses = new ArrayList<>();
//                    productEntity.getVariants().forEach(variantResponse -> {
//                        ProductVariantResponse productVariantResponse = new ProductVariantResponse();
//                        productVariantResponse.setId(variantResponse.getId());
//                        productVariantResponse.setPrice(variantResponse.getPrice());
//                        productVariantResponse.setVolume(variantResponse.getVolume());
//                        productVariantResponse.setUnit(variantResponse.getUnit());
//                        productVariantResponses.add(productVariantResponse);
//                    });
//                    productResponseDTO.setVariants(productVariantResponses);
//                }
//                top7BestSellers.add(productResponseDTO);
//            });
//        }
//        top7BestSellers.forEach(productResponseDTO -> productResponseDTO.setDescription(null));
//        return top7BestSellers;
//    }

    //## 7 sản phẩm có lượt mua cao nhất
    public List<ProductResponseDTO> findTop7FlashSale() {
        List<Long> productIds = productRepository.findTop7ProductIdsByStatusAndDeleted(Status.ACTIVE, false, PageRequest.of(0, 7));
        List<ProductResponseDTO> top7BestSellers = new ArrayList<>();

        if (!productIds.isEmpty()) {
            productIds.forEach(productEntity -> {
                top7BestSellers.add(productSearchRepository.getProductById(productEntity));
            });
        }

        if(!top7BestSellers.isEmpty()) {
            top7BestSellers.forEach(productResponseDTO -> {
                clearUnnecessaryFields(productResponseDTO);
                productResponseDTO.setCategory(null);
            });
        }
        return top7BestSellers;
    }


//    //## FRESH SKIN
//    // Show 3 san pham noi bat
//    public List<ProductResponseDTO> getProductsFeature() {
//        List<ProductEntity> productEntities = productRepository.findTop3ByStatusAndDeletedAndFeatured(Status.ACTIVE, false, true);
//        List<ProductResponseDTO> productResponseDTO = mapProductResponsesDTO(productEntities);
//        productResponseDTO.forEach(productResponseDTO1 -> {
//            productResponseDTO1.setCategory(null);
//            productResponseDTO1.setDescription(null);
//            productResponseDTO1.setSkinTypes(null);
//            productResponseDTO1.setIngredients(null);
//            productResponseDTO1.setOrigin(null);
//            productResponseDTO1.setSkinIssues(null);
//            productResponseDTO1.setUsageInstructions(null);
//        });
//        return productResponseDTO;
//    }

    //## FRESH SKIN
    // Show 3 san pham noi bat
    public List<ProductResponseDTO> getProductsFeature() {
//        List<ProductEntity> productEntities = productRepository.findTop3ByStatusAndDeletedAndFeatured(Status.ACTIVE, false, true);

        List<Long> list = productRepository.findTop3ByStatusAndDeletedAndFeatured(Status.ACTIVE, false,PageRequest.of(0, 3));

        List<ProductResponseDTO> productResponseDTO = new ArrayList<>();
        list.forEach(productEntity -> {
            productResponseDTO.add(productSearchRepository.getProductById(productEntity));
        });

        if(!productResponseDTO.isEmpty()) {
            productResponseDTO.forEach(productResponseDTO1 -> {
                productResponseDTO1.setCategory(null);
                productResponseDTO1.setSkinTypes(null);
                clearUnnecessaryFields(productResponseDTO1);
            });
        }

        return productResponseDTO;
    }



    //Tìm chi tiết Product bằng Slug
    public List<Map<String, Object>> getProductBySlug(String slug) {

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        // Lấy sản phẩm theo slug
        ProductResponseDTO productCategoryResponses = productSearchRepository.findBySlug(slug, "ACTIVE", false);

        if (productCategoryResponses == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        map.put("productDetail", productCategoryResponses);

        List<Long> ids = new ArrayList<>();

        productCategoryResponses.getCategory().forEach(productCategoryEntity -> ids.add(productCategoryEntity.getId()));


        List<ProductResponseDTO> productRelated = productSearchRepository.getProductByCategoryIDs(ids, "ACTIVE", false, 7);


        productRelated.removeIf(productRelaters -> productRelaters.getId().equals(productCategoryResponses.getId()));

        productRelated.forEach(productRelatedResponse -> {
            productRelatedResponse.setCategory(null);
            productRelatedResponse.setSkinTypes(null);
            productRelatedResponse.setBrand(null);
            clearUnnecessaryFields(productRelatedResponse);
        });
        map.put("productsRelated", productRelated);
        data.add(map);
        return data;
    }

//    //Tìm chi tiết Product bằng Slug
//    public List<Map<String, Object>> getProductBySlug(String slug) {
//
//
//        List<Map<String, Object>> data = new ArrayList<>();
//        Map<String, Object> map = new HashMap<>();
//
//        // Lấy sản phẩm theo slug
//        ProductEntity productEntity = productRepository.findBySlug(slug);
//
//        if (productEntity == null || productEntity.isDeleted() || productEntity.getStatus() != Status.ACTIVE) {
//            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        List<ProductCategoryResponse> productCategoryResponses = new ArrayList<>();
//
//
//        productEntity.getCategory().forEach(productCategoryEntity -> {
//            ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();
//            productCategoryResponse.setId(productCategoryEntity.getId());
//            productCategoryResponse.setTitle(productCategoryEntity.getTitle());
//            productCategoryResponse.setSlug(productCategoryEntity.getSlug());
//
//            if (productCategoryEntity.getParent() != null) {
//                ProductCategoryResponse parentCategoryResponse = new ProductCategoryResponse();
//                parentCategoryResponse.setId(productCategoryEntity.getParent().getId());
//                parentCategoryResponse.setTitle(productCategoryEntity.getParent().getTitle());
//                parentCategoryResponse.setSlug(productCategoryEntity.getParent().getSlug());
//                productCategoryResponse.setParent(parentCategoryResponse);
//            }
//
//
//            productCategoryResponses.add(productCategoryResponse);
//
//
//        });
//        ProductResponseDTO response = mapProductResponseDTO(productEntity);
//        response.setCategory(productCategoryResponses);
//        List<ProductVariantResponse> productVariantResponses = new ArrayList<>();
//        productEntity.getVariants().forEach(variant -> {
//            ProductVariantResponse variantResponse = new ProductVariantResponse();
//            variantResponse.setId(variant.getId());
//            variantResponse.setPrice(variant.getPrice());
//            variantResponse.setVolume(variant.getVolume());
//            variantResponse.setUnit(variant.getUnit());
//            productVariantResponses.add(variantResponse);
//        });
//        response.setVariants(productVariantResponses);
//        // Nhét sản phẩm chính vào map (bọc vào List)
//        map.put("productDetail", response);
//
//        // Tìm id của các category
//        List<Long> ids = new ArrayList<>();
//
//        productEntity.getCategory().forEach(productCategoryEntity -> ids.add(productCategoryEntity.getId()));
//
//
//        List<ProductEntity> productEntities = productRepository.findTop10ByCategory_IdIn(ids);
//
//
//        productEntities.removeIf(productEntity1 -> productEntity1.getId().equals(productEntity.getId()));
//
//
//        List<ProductResponseDTO> productRelatedResponses = mapProductResponsesDTO(productEntities);
//        productRelatedResponses.forEach(productRelatedResponse -> {
//            productRelatedResponse.setCategory(null);
//            productRelatedResponse.setSkinTypes(null);
//            productRelatedResponse.setBrand(null);
//            clearUnnecessaryFields(productRelatedResponse);
//        });
//        map.put("productsRelated", productRelatedResponses);
//        data.add(map);
//        return data;
//    }

    //hàm này là con của hàm getProductCategoryBySlug => có giới hạn sản phẩm được trả ra
    private Map<String, Object> getLimitProductByCategorySlug(int maxSize, int size, int page, String sortValue, String sortDirection, String slug, List<String> brand, List<String> category, List<String> skinTypes, double minPrice, double maxPrice) {
        Map<Long, ProductCategoryResponse> productCategoryResponseMap = new HashMap<>();
        Map<Long, ProductBrandResponse> productBrandResponseMap = new HashMap<>();
        Map<Long, SkinTypeResponse> skinTypeResponseMap = new HashMap<>();

        Pageable limitPageable = PageRequest.of(0, maxSize);

        Specification<ProductEntity> filterValues = findByParentCategorySlug(slug)
                .and(isNotDeleted())
                .and(filterByStatus(Status.ACTIVE));
        List<ProductEntity> productValues = productRepository.findAll(filterValues, limitPageable).getContent();

        productValues.forEach(productEntity -> {
            if (productEntity.getCategory() != null) {
                productEntity.getCategory().forEach(productCategory -> {
                    ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();
                    productCategoryResponse.setTitle(productCategory.getTitle());
                    productCategoryResponse.setId(productCategory.getId());
                    productCategoryResponseMap.putIfAbsent(productCategory.getId(), productCategoryResponse);
                });
            }

            if (productEntity.getBrand() != null) {
                ProductBrandResponse productBrandResponse = new ProductBrandResponse();
                productBrandResponse.setTitle(productEntity.getBrand().getTitle());
                productBrandResponseMap.putIfAbsent(productEntity.getBrand().getId(), productBrandResponse);
            }

            if (productEntity.getSkinTypes() != null) {
                productEntity.getSkinTypes().forEach(skinType -> {
                    SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
                    skinTypeResponse.setType(skinType.getType());
                    skinTypeResponseMap.putIfAbsent(skinType.getId(), skinTypeResponse);
                });
            }
        });


        Specification<ProductEntity> specification = findByParentCategorySlug(slug)
                .and(isNotDeleted())
                .and(filterByStatus(Status.ACTIVE));

        if (brand != null) {
            specification = specification.and(filterByBrand(brand));
        }

        if (category != null) {
            specification = specification.and(filterByCategory(category));
        }

        if (skinTypes != null) {

            specification = specification.and(filterBySkinType(skinTypes));
        }


        if (sortValue != null) {
            if (!sortValue.isEmpty() && sortValue.equals("title")) {
                specification = specification.and(sortByTitle(getSortDirection(sortDirection)));
            } else if (!sortValue.isEmpty() && sortValue.equals("price")) {
                specification = specification.and(sortByPrice(getSortDirection(sortDirection)));
            }
        }


        if (minPrice > 0 && maxPrice > 0) {
            specification = specification.and(filterByPrice(minPrice, maxPrice));
        }


        List<ProductEntity> limitedProducts = productRepository.findAll(specification, limitPageable).getContent();

        int p = (page > 0) ? page - 1 : 0;
        int totalItems = limitedProducts.size();
        int totalPage = (int) Math.ceil((double) totalItems / size);
        List<ProductEntity> paginatedProducts;

        if (page > totalPage) {
            paginatedProducts = Collections.emptyList();
        } else {
            int fromIndex = p * size;
            int toIndex = Math.min(fromIndex + size, limitedProducts.size());
            paginatedProducts = limitedProducts.subList(fromIndex, toIndex);
        }


        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("page", page);
        pageMap.put("totalItems", totalItems);
        pageMap.put("totalPages", totalPage);
        pageMap.put("pageSize", size);


        Map<String, Object> map = new HashMap<>();

        List<ProductCategoryResponse> categoryResponses = new ArrayList<>(productCategoryResponseMap.values());
        List<ProductBrandResponse> brandResponses = new ArrayList<>(productBrandResponseMap.values());
        List<SkinTypeResponse> skinTypeResponses = new ArrayList<>(skinTypeResponseMap.values());
        List<ProductResponseDTO> productResponseDTOs = mapProductResponsesDTO(paginatedProducts);

        productResponseDTOs.forEach(productResponseDTO -> {
            productResponseDTO.setCategory(null);
            productResponseDTO.setSkinTypes(null);
            clearUnnecessaryFields(productResponseDTO);
        });


        if (slug.equals("san-pham-moi")) {
            map.put("title", "Sản Phẩm Mới");
        } else if (slug.equals("khuyen-mai-hot")) {
            map.put("title", "Khuyến Mãi Hot");
        }

        map.put("products", productResponseDTOs);
        map.put("categories", categoryResponses);
        map.put("brands", brandResponses);
        map.put("skinTypes", skinTypeResponses);
        map.put("page", pageMap);

        // Trả về kết quả
        return map;
    }


    public Map<String, Object> getProductByCategoryOrBrandSlug(int size, int page, String sortValue, String sortDirection, String slug, List<String> brand, List<String> category, List<String> skinTypes, double minPrice, double maxPrice) {

        int maxSize = 36;

        if (slug.equals("khuyen-mai-hot")) {
            maxSize = 30;
        }


        if (slug.equals("san-pham-moi") || slug.equals("khuyen-mai-hot")) {
            return getLimitProductByCategorySlug(maxSize, size, page, sortValue, sortDirection, slug, brand, category, skinTypes, minPrice, maxPrice);
        }

        // Lấy danh sách brand,category và skintype có trong product

        Map<Long, ProductCategoryResponse> productCategoryResponseMap = new HashMap<>();
        Map<Long, ProductBrandResponse> productBrandResponseMap = new HashMap<>();
        Map<Long, SkinTypeResponse> skinTypeResponseMap = new HashMap<>();

        Specification<ProductEntity> filterSpec = findByParentCategorySlug(slug).or(findByBrandSlug(slug))
                .and(isNotDeleted())
                .and(filterByStatus(Status.ACTIVE));

        List<ProductEntity> filteredProducts = productRepository.findAll(filterSpec);

        filteredProducts.forEach(productEntity -> {
            if (productEntity.getCategory() != null) {
                productEntity.getCategory().forEach(productCategory -> {
                    ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();
                    productCategoryResponse.setTitle(productCategory.getTitle());
                    productCategoryResponse.setId(productCategory.getId());
                    productCategoryResponseMap.putIfAbsent(productCategory.getId(), productCategoryResponse);
                });
            }

            if (productEntity.getBrand() != null) {
                ProductBrandResponse productBrandResponse = new ProductBrandResponse();
                productBrandResponse.setTitle(productEntity.getBrand().getTitle());
                productBrandResponseMap.putIfAbsent(productEntity.getBrand().getId(), productBrandResponse);
            }

            if (productEntity.getSkinTypes() != null) {
                productEntity.getSkinTypes().forEach(skinType -> {
                    SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
                    skinTypeResponse.setType(skinType.getType());
                    skinTypeResponseMap.putIfAbsent(skinType.getId(), skinTypeResponse);
                });
            }
        });


        Specification<ProductEntity> specification = findByParentCategorySlug(slug).or(findByBrandSlug(slug))
                .and(isNotDeleted())
                .and(filterByStatus(Status.ACTIVE));

        if (brand != null) {
            specification = specification.and(filterByBrand(brand));
        }

        if (category != null) {
            specification = specification.and(filterByCategory(category));
        }

        if (skinTypes != null) {

            specification = specification.and(filterBySkinType(skinTypes));
        }

        if (sortValue.equals("title")) {
            specification = specification.and(sortByTitle(getSortDirection(sortDirection)));
        } else if (sortValue.equals("price")) {
            specification = specification.and(sortByPrice(getSortDirection(sortDirection)));
        } else if (sortValue.equals("position")) {
            specification = specification.and(sortByPosition(getSortDirection(sortDirection)));

        }

        if (minPrice > 0 && maxPrice > 0) {
            specification = specification.and(filterByPrice(minPrice, maxPrice));
        }


        int p = (page > 0) ? page - 1 : 0;

        Pageable pageable = PageRequest.of(p, size);


        Page<ProductEntity> productEntityPage = productRepository.findAll(specification, pageable);

        Map<String, Object> map = new HashMap<>();

        List<ProductCategoryResponse> categoryResponses = new ArrayList<>(productCategoryResponseMap.values());
        List<ProductBrandResponse> brandResponses = new ArrayList<>(productBrandResponseMap.values());
        List<SkinTypeResponse> skinTypeResponses = new ArrayList<>(skinTypeResponseMap.values());
        List<ProductResponseDTO> productResponseDTOs = mapProductResponsesDTO(productEntityPage.getContent());

        productResponseDTOs.forEach(productResponseDTO -> {
            productResponseDTO.setCategory(null);
            productResponseDTO.setSkinTypes(null);
            clearUnnecessaryFields(productResponseDTO);
        });


        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("page", productEntityPage.getNumber() + 1);
        pageMap.put("totalItems", productEntityPage.getTotalElements());
        pageMap.put("totalPages", productEntityPage.getTotalPages());
        pageMap.put("pageSize", productEntityPage.getSize());

        ProductCategoryEntity titleCate = productCategoryRepository.findCategoryBySlug(slug);
        ProductBrandEntity titleBrand = productBrandRepository.findBySlug(slug);
        if (titleCate != null) {
            map.put("title", titleCate.getTitle());
        } else if (titleBrand != null) {
            map.put("title", titleBrand.getTitle());
        } else if (slug.equals("thuong-hieu")) {
            map.put("title", "Thương Hiệu");
        } else if (slug.equals("tat-ca-san-pham")) {
            map.put("title", "Tất cả sản phẩm");
        }

        map.put("products", productResponseDTOs);
        map.put("categories", categoryResponses);
        map.put("brands", brandResponses);
        map.put("skinTypes", skinTypeResponses);
        map.put("page", pageMap);

        // Trả về kết quả
        return map;
    }

    //hàm này dùng để search

    public Map<String, Object> getProductsByKeyword(String keyword, int size, int page) {
        Map<String, Object> map = new HashMap<>();

        // Xử lý page
        int p = (page > 0) ? page - 1 : 0;

        //Tìm kiếm danh sách sản phẩm
        CompletableFuture<List<ProductResponseDTO>> productsFuture = CompletableFuture.supplyAsync(() -> productSearchRepository.searchByTitle(keyword, p, size));

        //Tìm kiếm tổng số sản phẩm
        CompletableFuture<Integer> totalItemsFuture = CompletableFuture.supplyAsync(() -> productSearchRepository.searchByTitle(keyword, 0, 50).size());

        CompletableFuture.allOf(productsFuture, totalItemsFuture).join();

        List<ProductResponseDTO> filteredProducts = productsFuture.join();
        int totalItem = totalItemsFuture.join();

        // Nếu không tìm thấy sản phẩm, trả về thông báo
        if (filteredProducts.isEmpty()) {
            map.put("messageNotFound", "Rất tiếc, không tìm thấy sản phẩm từ " + keyword);
            return map;
        }


        int totalPages = (int) Math.ceil((double) totalItem / size);

        filteredProducts.forEach(productResponseDTO -> {
            productResponseDTO.setCategory(null);
            productResponseDTO.setSkinTypes(null);
            clearUnnecessaryFields(productResponseDTO);
        });


        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("page", p + 1);
        pageMap.put("totalItems", totalItem);
        pageMap.put("totalPages", totalPages);
        pageMap.put("pageSize", size);


        map.put("title", keyword);
        map.put("products", filteredProducts);
        map.put("page", pageMap);


        return map;
    }

    //    @Cacheable(value = "productSuggestions", key = "#request", condition = "#request != null and #request.length() > 2")
    public List<ProductResponseDTO> suggestProduct(String request) {
        List<ProductResponseDTO> result = productSearchRepository.searchByTitle(request, 4);
        result.forEach(productResponseDTO -> {
            clearUnnecessaryFields(productResponseDTO);
            productResponseDTO.setBrand(null);
            productResponseDTO.setCategory(null);
            productResponseDTO.setSkinTypes(null);
        });
        return result;
    }


    private void clearUnnecessaryFields(ProductResponseDTO productResponseDTO) {
        productResponseDTO.setOrigin(null);
        productResponseDTO.setIngredients(null);
        productResponseDTO.setUsageInstructions(null);
        productResponseDTO.setBenefits(null);
        productResponseDTO.setSkinIssues(null);
        productResponseDTO.setDescription(null);
    }


    // Hàm Map thủ công danh sách ProductResponseDTO
    private List<ProductResponseDTO> mapProductResponsesDTO(List<ProductEntity> productEntities) {

        // Map danh sách Product
        List<ProductResponseDTO> productResponseDTOs = productMapper.productToProductResponsesDTO(productEntities);

        // Map quan hệ của từng Product
        for (int i = 0; i < productEntities.size(); i++) {
            ProductEntity product = productEntities.get(i);
            ProductResponseDTO dto = productResponseDTOs.get(i);

            // Map thương hiệu của Product
            if (product.getBrand() != null) {
                ProductBrandResponse brandResponse = new ProductBrandResponse();
                brandResponse.setTitle(product.getBrand().getTitle());
                dto.setBrand(brandResponse);
            }

            // Map loại da của Product
            if (product.getSkinTypes() != null) {
                List<SkinTypeResponse> skinTypeResponses = new ArrayList<>();
                for (SkinTypeEntity skinType : product.getSkinTypes()) {
                    SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
                    skinTypeResponse.setType(skinType.getType());
                    skinTypeResponses.add(skinTypeResponse);
                }
                dto.setSkinTypes(skinTypeResponses);
            }

            // Map danh mục của Product
            if (product.getCategory() != null) {
                List<ProductCategoryResponse> categoryResponses = new ArrayList<>();
                for (ProductCategoryEntity category : product.getCategory()) {
                    ProductCategoryResponse categoryResponse = new ProductCategoryResponse();
                    categoryResponse.setId(category.getId());
                    categoryResponse.setTitle(category.getTitle());
                    categoryResponses.add(categoryResponse);
                }
                dto.setCategory(categoryResponses);
            }

            // Map danh sách biến thể của Product
            if (product.getVariants() != null) {
                List<ProductVariantResponse> variantResponses = new ArrayList<>();
                for (ProductVariantEntity variant : product.getVariants()) {
                    ProductVariantResponse variantResponse = new ProductVariantResponse();
                    variantResponse.setId(variant.getId());
                    variantResponse.setUnit(variant.getUnit());
                    variantResponse.setVolume(variant.getVolume());
                    variantResponse.setPrice(variant.getPrice());
                    variantResponses.add(variantResponse);
                }
                dto.setVariants(variantResponses);
            }

            // Xóa các trường không cần thiết
            dto.setFeatured(null);
            dto.setStatus(null);
            dto.setCreatedAt(null);
            dto.setUpdatedAt(null);
            dto.setPosition(null);
        }

        return productResponseDTOs;
    }


    // Hàm Map thủ công 1 ProductResponse
    private ProductResponseDTO mapProductResponseDTO(ProductEntity productEntity) {
        // Map Product
        ProductResponseDTO productResponseDTO = productMapper.productToProductResponseDTO(productEntity);

        // Map danh sách variants
        if (productResponseDTO.getVariants() != null) {
            List<ProductVariantResponse> productVariantResponses = new ArrayList<>();
            for (ProductVariantResponse variant : productResponseDTO.getVariants()) {
                ProductVariantResponse newVariant = new ProductVariantResponse();
                newVariant.setId(variant.getId());
                newVariant.setUnit(variant.getUnit());
                newVariant.setVolume(variant.getVolume());
                newVariant.setPrice(variant.getPrice());
                productVariantResponses.add(newVariant);
            }
            productResponseDTO.setVariants(productVariantResponses);
        }

        // Lấy thương hiệu của Product
        if (productEntity.getBrand() != null) {
            ProductBrandResponse productBrandResponse = new ProductBrandResponse();
            productBrandResponse.setTitle(productEntity.getBrand().getTitle());
            productResponseDTO.setBrand(productBrandResponse);
        }

        // Lấy loại da của Product
        if (productEntity.getSkinTypes() != null) {
            List<SkinTypeResponse> skinTypeResponses = new ArrayList<>();
            for (SkinTypeEntity skinType : productEntity.getSkinTypes()) {
                SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
                skinTypeResponse.setType(skinType.getType());
                skinTypeResponses.add(skinTypeResponse);
            }
            productResponseDTO.setSkinTypes(skinTypeResponses);
        }

        // Lấy danh sách danh mục của Product
        if (productEntity.getCategory() != null) {
            List<ProductCategoryResponse> categoryResponses = new ArrayList<>();
            for (ProductCategoryEntity category : productEntity.getCategory()) {
                if (category.getParent() != null) {
                    for (ProductEntity product : category.getParent().getProducts()) {
                        if (Objects.equals(product.getId(), productEntity.getId())) {
                            ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();
                            productCategoryResponse.setTitle(category.getTitle());
                            productCategoryResponse.setId(category.getId());
                            categoryResponses.add(productCategoryResponse);
                        }
                    }
                }
            }
            productResponseDTO.setCategory(categoryResponses);
        }

        // Xóa các giá trị không cần thiết
        productResponseDTO.setStatus(null);
        productResponseDTO.setCreatedAt(null);
        productResponseDTO.setUpdatedAt(null);
        productResponseDTO.setPosition(null);

        return productResponseDTO;
    }


    //hàm nay để map riêng vào searchPublic

    // Hàm Map danh sách ProductResponseDTO từ danh sách ProductEntity
    private List<ProductResponseDTO> mapProductIndexResponsesDTO(List<ProductEntity> productEntities) {
        List<ProductResponseDTO> productResponseDTOs = productMapper.productToProductResponsesDTO(productEntities);

        IntStream.range(0, productEntities.size()).forEach(i -> {
            ProductEntity product = productEntities.get(i);
            ProductResponseDTO dto = productResponseDTOs.get(i);

            // Map thương hiệu
            if (product.getBrand() != null) {
                ProductBrandResponse brandResponse = new ProductBrandResponse();
                brandResponse.setId(product.getBrand().getId());
                brandResponse.setTitle(product.getBrand().getTitle());
                brandResponse.setSlug(product.getBrand().getSlug());
                dto.setBrand(brandResponse);
            }

//            if(product.getDiscount() != null) {
//                DiscountResponse discountResponse = new DiscountResponse();
//                discountResponse.getDiscountType();
//            }

//            if (product.getReviews() != null) {
//                List<ReviewResponse> reviewResponses = product.getReviews().stream()
//                        .filter(review -> review.getParent() == null) // Chỉ lấy root reviews
//                        .map(reviewService::convertToReviewResponse) // Chuyển đổi từng review
//                        .collect(Collectors.toList());
//
//                dto.setReviews(reviewResponses); // Gán vào DTO
//            }

            // Map danh mục sản phẩm
            if (product.getCategory() != null) {
                dto.setCategory(product.getCategory().stream().map(category -> {
                    ProductCategoryResponse categoryResponse = new ProductCategoryResponse();
                    categoryResponse.setId(category.getId());
                    categoryResponse.setTitle(category.getTitle());
                    categoryResponse.setSlug(category.getSlug());

                    if (category.getParent() != null) {
                        ProductCategoryResponse parentCategoryResponse = new ProductCategoryResponse();
                        parentCategoryResponse.setId(category.getParent().getId());
                        parentCategoryResponse.setTitle(category.getParent().getTitle());
                        parentCategoryResponse.setSlug(category.getParent().getSlug());
                        categoryResponse.setParent(parentCategoryResponse);
                    }


                    return categoryResponse;
                }).collect(Collectors.toList()));
            }

            // Map loại da của Product
            if (product.getSkinTypes() != null) {
                dto.setSkinTypes(product.getSkinTypes().stream()
                        .map(skinType -> {
                            SkinTypeResponse response = new SkinTypeResponse();
                            response.setId(skinType.getId());
                            response.setType(skinType.getType());
                            return response;
                        })
                        .collect(Collectors.toList()));
            }

            // Map danh sách biến thể của Product
            if (product.getVariants() != null) {
                dto.setVariants(product.getVariants().stream()
                        .map(variant -> {
                            ProductVariantResponse variantResponse = new ProductVariantResponse();
                            variantResponse.setId(variant.getId());
                            variantResponse.setPrice(variant.getPrice());
                            variantResponse.setVolume(variant.getVolume());
                            variantResponse.setUnit(variant.getUnit());
                            return variantResponse;
                        })
                        .collect(Collectors.toList()));
            }
        });

        return productResponseDTOs;
    }

    private ProductResponseDTO mapProductIndexResponsesDTO(ProductEntity product) {
        ProductResponseDTO dto = productMapper.productToProductResponseDTO(product);

        // Map thương hiệu của Product
        if (product.getBrand() != null) {
            ProductBrandResponse brandResponse = new ProductBrandResponse();
            brandResponse.setId(product.getBrand().getId());
            brandResponse.setTitle(product.getBrand().getTitle());
            brandResponse.setSlug(product.getBrand().getSlug());
            dto.setBrand(brandResponse);
        }

//        // Map danh sách review
//        if (product.getReviews() != null) {
//            dto.setReviews(product.getReviews().stream()
//                    .map(reviewService::convertToReviewResponse)
//                    .collect(Collectors.toList()));
//        }

        // Map danh mục của Product
        if (product.getCategory() != null) {
            dto.setCategory(product.getCategory().stream()
                    .map(category -> {
                        ProductCategoryResponse categoryResponse = new ProductCategoryResponse();
                        categoryResponse.setId(category.getId());
                        categoryResponse.setTitle(category.getTitle());
                        categoryResponse.setSlug(category.getSlug());

                        // Map danh mục cha nếu có
                        if (category.getParent() != null) {
                            ProductCategoryResponse parentCategoryResponse = new ProductCategoryResponse();
                            parentCategoryResponse.setId(category.getParent().getId());
                            parentCategoryResponse.setTitle(category.getParent().getTitle());
                            parentCategoryResponse.setSlug(category.getParent().getSlug());
                            categoryResponse.setParent(parentCategoryResponse);
                        }
                        return categoryResponse;
                    })
                    .collect(Collectors.toList()));
        }

        // Map loại da của Product
        if (product.getSkinTypes() != null) {
            dto.setSkinTypes(product.getSkinTypes().stream()
                    .map(skinType -> {
                        SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
                        skinTypeResponse.setId(skinType.getId());
                        skinTypeResponse.setType(skinType.getType());
                        return skinTypeResponse;
                    })
                    .collect(Collectors.toList()));
        }

        // Map danh sách biến thể của Product
        if (product.getVariants() != null) {
            dto.setVariants(product.getVariants().stream()
                    .map(variant -> {
                        ProductVariantResponse variantResponse = new ProductVariantResponse();
                        variantResponse.setId(variant.getId());
                        variantResponse.setPrice(variant.getPrice());
                        variantResponse.setVolume(variant.getVolume());
                        variantResponse.setUnit(variant.getUnit());
                        return variantResponse;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public boolean indexProduct() {
        List<ProductEntity> productEntities = productRepository.findAll();
        List<ProductResponseDTO> responseDTOS = mapProductIndexResponsesDTO(productEntities);

        responseDTOS.forEach(productSearchRepository::indexProduct);

        return false;
    }

    //    Hỗ trợ cho lấy 7 loại danh mục sản phảm cô lộ trình da
    public Page<ProductRoutineDTO> getProductsBySkinTypeAndCategories(Long skinTypeId, int page, int size) {
        List<String> orderedCategories = List.of(
                "Nước tẩy trang",
                "Sữa rửa mặt",
                "Tẩy tế bào chết",
                "Toner / Nước cân bằng da",
                "Hỗ trợ trị mụn",
                "Serum / Tinh Chất",
                "Dưỡng ẩm",
                "Chống nắng da mặt"
        );

        String sql = """
                    WITH RankedProducts AS (
                        SELECT p.*,
                               c.title as category_title,
                               ROW_NUMBER() OVER (PARTITION BY c.title ORDER BY p.product_id) as rn
                        FROM product p
                        JOIN product_category pc ON p.product_id = pc.productid
                        JOIN category c ON pc.categoryid = c.id
                        JOIN product_skin_type pst ON p.product_id = pst.product_id
                        WHERE pst.skin_type_id = :skinTypeId
                        AND c.title IN :categories
                        AND p.deleted = false
                        AND p.status = 'ACTIVE'
                    )
                    SELECT * FROM RankedProducts
                    WHERE rn = 1
                    ORDER BY FIELD(category_title, :orderList)
                """;

        Query query = entityManager.createNativeQuery(sql, ProductEntity.class)
                .setParameter("skinTypeId", skinTypeId)
                .setParameter("categories", orderedCategories)
                .setParameter("orderList", orderedCategories.stream()
                        .collect(Collectors.joining("','", "'", "'"))); // Format: 'cat1','cat2',...

        List<ProductEntity> products = query.getResultList();

        // Apply pagination
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());

        List<ProductRoutineDTO> dtoList = products.subList(start, end)
                .stream()
                .map(this::mapToRoutineDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, products.size());
    }

    private ProductRoutineDTO mapToRoutineDTO(ProductEntity product) {
        ProductRoutineDTO dto = new ProductRoutineDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setSlug(product.getSlug());
        dto.setThumbnail(product.getThumbnail());

        if (product.getBrand() != null) {
            ProductBrandResponse brandResponse = new ProductBrandResponse();
            brandResponse.setId(product.getBrand().getId());
            brandResponse.setTitle(product.getBrand().getTitle());
            brandResponse.setSlug(product.getBrand().getSlug());
            dto.setBrand(brandResponse);
        }

        if (product.getCategory() != null) {
            List<ProductCategoryResponse> categoryResponses = product.getCategory().stream()
                    .map(category -> {
                        ProductCategoryResponse response = new ProductCategoryResponse();
                        response.setId(category.getId());
                        response.setTitle(category.getTitle());
                        response.setSlug(category.getSlug());
                        return response;
                    })
                    .collect(Collectors.toList());
            dto.setCategory(categoryResponses);
        }

        if (product.getVariants() != null) {
            List<ProductVariantResponse> variantResponses = product.getVariants().stream()
                    .map(variant -> {
                        ProductVariantResponse response = new ProductVariantResponse();
                        response.setId(variant.getId());
                        response.setPrice(variant.getPrice());
                        response.setVolume(variant.getVolume());
                        response.setUnit(variant.getUnit());
                        return response;
                    })
                    .collect(Collectors.toList());
            dto.setVariants(variantResponses);
        }

        return dto;
    }

    //data dashboard
    public long countProduct() {
        return productRepository.countByStatusAndDeleted(Status.ACTIVE, false);
    }

    //top sản phẩm bán chạy

    public List<ProductResponseDTO> top10SellingProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Long> result = productRepository.findTop10SellingProducts(pageRequest);
        List<ProductResponseDTO> responseDTOS = new ArrayList<>();
        result.forEach(productId -> {
            responseDTOS.add(productSearchRepository.getProductById(productId));
        });

        responseDTOS.forEach(productResponseDTO -> {
            clearUnnecessaryFields(productResponseDTO);
            productResponseDTO.setSkinTypes(null);
        });
        return responseDTOS;
    }
}


