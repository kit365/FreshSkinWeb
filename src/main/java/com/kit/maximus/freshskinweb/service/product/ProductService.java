package com.kit.maximus.freshskinweb.service.product;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.*;
import com.kit.maximus.freshskinweb.repository.search.ProductSearchRepository;
import com.kit.maximus.freshskinweb.service.BaseService;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    ProductVariantRepository productVariantRepository;

    SkinTypeRepository skinTypeRepository;


    Cloudinary cloudinary;

    ProductSearchRepository productSearchRepository;

    // Biến lưu toàn bộ các danh mục sản phẩm cho các bước trong lộ trình chăm sóc da
    private static final List<String> CATEGORY_KEYWORDS = Arrays.asList(
            "Nước tẩy trang",
            "Sữa rữa mặt",
            "Toner",
            "Serum / Tinh Chất",
            "Dưỡng ẩm",
            "Chống nắng da mặt"
    );

    // Biến lưu toàn bộ loại da cho các bước trong lộ trình chăm sóc da
    private static final List<String> SKIN_TYPE_KEYWORDS = Arrays.asList(
            "da dầu",
            "da khô",
            "da nhạy cảm",
            "da hỗn hợp",
            "da thường"
    );




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

    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
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

    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
    public String update(long id, String status, Integer position, String statusEdit) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        System.out.println("status: " + status + ", position: " + position + ", statusEdit: " + statusEdit);

        if ("editStatus".equalsIgnoreCase(statusEdit)) {
            if (status != null) { // Kiểm tra nếu status không bị null
                Status statusEnum = getStatus(status);
                productEntity.setStatus(statusEnum);
                productRepository.save(productEntity);
                productSearchRepository.update(id, status);
                return "Cập nhật trạng thái thành công";
            }
            return "Trạng thái không được để trống!";
        } else if ("editPosition".equalsIgnoreCase(statusEdit)) {
            if (position != 0) { // Kiểm tra null trước khi gán
                productEntity.setPosition(position);
                productRepository.save(productEntity);
                productSearchRepository.update(id, position);
                return "Cập nhật vị trí thành công";
            }
            return "Vị trí không được để trống!";
        }

        return "Cập nhật thất bại";
    }



    //noted: thêm set thumb vao entity sau khi update
    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
    @Override
    public ProductResponseDTO update(Long id, UpdateProductRequest request) {
        ProductEntity listProduct = getProductEntityById(id);


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
                    if (productVariantEntity.getPrice().compareTo(BigDecimal.ZERO) < 0)
                        productVariantEntity.setPrice(BigDecimal.ZERO);
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


        if ((request.getImage() != null && !request.getImage().isEmpty()) ||
                (request.getThumbnail() != null && !request.getThumbnail().isEmpty())) {

            // Lấy danh sách ảnh cũ từ listProduct
            List<String> oldImages = listProduct.getThumbnail() != null ?
                    new ArrayList<>(listProduct.getThumbnail()) : new ArrayList<>();

            // Lấy danh sách tên file mới từ request
            List<String> newThumbnails = request.getThumbnail() != null ?
                    request.getThumbnail().stream().map(MultipartFile::getOriginalFilename).toList() :
                    new ArrayList<>();

            // Xóa ảnh cũ không còn trong danh sách mới
            for (String oldUrl : oldImages) {
                if (!newThumbnails.contains(oldUrl)) {
                    try {
                        deleteImageFromCloudinary(oldUrl); // Xóa khỏi Cloudinary
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete image: " + oldUrl, e);
                    }
                }
            }

            // Danh sách mới giữ lại các ảnh chưa bị xóa
            List<String> updatedThumbnails = new ArrayList<>(newThumbnails);

            // Thêm ảnh mới nếu có
            if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
                int count = 0;
                for (MultipartFile file : request.getThumbnail()) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                            updatedThumbnails.add(url);
                        } catch (IOException e) {
                            log.error("Failed to upload image: {}", e.getMessage());
                            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
                        }
                    }
                }
            }

            // Cập nhật lại danh sách ảnh trong listProduct
            listProduct.setThumbnail(updatedThumbnails);

        } else {
            // Nếu không có ảnh mới từ FE => Xóa hết ảnh cũ
            if (listProduct.getThumbnail() != null && !listProduct.getThumbnail().isEmpty()) {
                for (String oldUrl : listProduct.getThumbnail()) {
                    try {
                        deleteImageFromCloudinary(oldUrl); // Xóa toàn bộ hình ảnh
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete image: " + oldUrl, e);
                    }
                }
            }
            listProduct.setThumbnail(null); // Xóa danh sách ảnh trong DB
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
    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductEntity> productEntities = productRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productRepository.saveAll(productEntities);
            id.forEach(ids -> productSearchRepository.update(ids, status));
            return "Cập nhật trạng thái sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productRepository.saveAll(productEntities);
            id.forEach(ids -> productSearchRepository.update(ids, true));
            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productRepository.saveAll(productEntities);
            id.forEach(ids -> productSearchRepository.update(ids, false));
            return "Phục hồi thành công";
        }
        return "Cập nhật thất bại";
    }


    /*
       Xóa(cứng) 1 sản phẩm
       input: long id
       output: boolean
     */
    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
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
    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
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
    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
    @Override
    public boolean deleteTemporarily(Long id) {
        ProductEntity productEntity = getProductEntityById(id);

        log.info("Delete temporarily : {}", id);
        productEntity.setDeleted(true);
        productRepository.save(productEntity);
        productSearchRepository.update(id, true);
        return true;
    }


    /*
     Phục hồi: 1 sản phẩm
     - Khôi phục trạng thái sản phẩm(ACTIVE) và thay đô DELETE(False)
     input: long id
     output: boolean
   */
    @CacheEvict(value = {"productsFeature", "filteredCategories", "getProductByCategoryOrBrandSlug", "productGetTrash", "productGetAll"}, allEntries = true)
    @Override
    public boolean restore(Long id) {
        ProductEntity productEntity = getProductEntityById(id);
        productEntity.setDeleted(false);
        productRepository.save(productEntity);
        productSearchRepository.update(id, false);
        return true;
    }

    @Override
    public ProductResponseDTO showDetail(Long id) {
        ProductEntity productEntity = getProductEntityById(id);
        ProductResponseDTO productResponseDTO = productMapper.productToProductResponseDTO(productEntity);

        productResponseDTO.setBrand(getProductBrandResponse(productEntity));
        productResponseDTO.setCategory(getProductCategoryResponses(productEntity));
        productResponseDTO.setSkinTypes(getSkinTypeResponses(productEntity));

        if (productEntity.getVariants() != null) {
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


    @Cacheable(value = "productGetAll", key = "#page + '-' + #size + '-' + #sortKey + '-' + #sortDirection + '-' + #status + '-' + #keyword")
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


    @Cacheable(value = "productGetTrash", key = "#page + '-' + #size + '-' + #sortKey + '-' + #sortDirection + '-' + #status + '-' + #keyword")
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
     List<Long> productIds = productRepository.findTop7ProductIdsByStatusAndDeleted(Status.ACTIVE, false, PageRequest.of(0, 7, Sort.by(Sort.Direction.DESC, "position")));
        List<ProductResponseDTO> top7BestSellers = new ArrayList<>();

        if (!productIds.isEmpty()) {
            productIds.forEach(productEntity -> {
                top7BestSellers.add(productSearchRepository.getProductById(productEntity));
            });
        }

        if (!top7BestSellers.isEmpty()) {
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
    @Cacheable(value = "productsFeature")
    public List<ProductResponseDTO> getProductsFeature() {
//        List<ProductEntity> productEntities = productRepository.findTop3ByStatusAndDeletedAndFeatured(Status.ACTIVE, false, true);

List<Long> list = productRepository.findTop3ByStatusAndDeletedAndFeatured(Status.ACTIVE, false, PageRequest.of(0, 9, Sort.by(Sort.Direction.DESC, "position")));

        List<ProductResponseDTO> productResponseDTO = new ArrayList<>();
        list.forEach(productEntity -> {
            productResponseDTO.add(productSearchRepository.getProductById(productEntity));
        });

        if (!productResponseDTO.isEmpty()) {
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


    private Map<String, Object> getLimitProductByCategorySlug(int maxSize, int size, int page, String sortValue, String sortDirection, String slug, List<String> brand, List<String> category, List<String> skinTypes, double minPrice, double maxPrice) {
        Map<Long, ProductCategoryResponse> productCategoryResponseMap = new HashMap<>();
        Map<Long, ProductBrandResponse> productBrandResponseMap = new HashMap<>();
        Map<Long, SkinTypeResponse> skinTypeResponseMap = new HashMap<>();

        Pageable limitPageable = PageRequest.of(0, maxSize);
        Specification<ProductEntity> filterValues;
        List<ProductEntity> productValues = new ArrayList<>();
        if(!slug.equals("top-ban-chay")) {
            filterValues = findByParentCategorySlug(slug)
                    .and(isNotDeleted())
                    .and(filterByStatus(Status.ACTIVE));
            productValues = productRepository.findAll(filterValues, limitPageable).getContent();
        }

        if(slug.equals("top-ban-chay")){
            filterValues = findTopSellingProducts()
                    .and(isNotDeleted())
                    .and(filterByStatus(Status.ACTIVE));
            productValues = productRepository.findAll(filterValues, limitPageable).getContent();
        }

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




        Specification<ProductEntity> specification = null;
        if(!slug.equals("top-ban-chay")) {
            specification = findByParentCategorySlug(slug)
                    .and(isNotDeleted())
                    .and(filterByStatus(Status.ACTIVE));
        }

        if(slug.equals("top-ban-chay")){
            specification = findTopSellingProducts()
                    .and(isNotDeleted())
                    .and(filterByStatus(Status.ACTIVE));
        }

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
        } else if(slug.equals("top-ban-chay")){
            map.put("title", "Top Bán Chạy");
        }

        map.put("products", productResponseDTOs);
        map.put("categories", categoryResponses);
        map.put("brands", brandResponses);
        map.put("skinTypes", skinTypeResponses);
        map.put("page", pageMap);

        // Trả về kết quả
        return map;
    }


    @Cacheable(value = "getProductByCategoryOrBrandSlug",
            key = "#size + '_' + #page + '_' + (#sortValue ?: 'none') + '_' + (#sortDirection ?: 'none') + '_' + (#slug ?: 'none') + '_' + T(java.lang.String).join(',', #brand ?: '') + '_' + T(java.lang.String).join(',', #category ?: '') + '_' + T(java.lang.String).join(',', #skinTypes ?: '') + '_' + #minPrice + '_' + #maxPrice")
    public Map<String, Object> getProductByCategoryOrBrandSlug(int size, int page, String sortValue, String sortDirection, String slug, List<String> brand, List<String> category, List<String> skinTypes, double minPrice, double maxPrice) {

        int maxSize = 36;

        if (slug.equals("khuyen-mai-hot")) {
            maxSize = 30;
        }

        if(slug.equals("top-ban-chay")) {
            maxSize = 12;
        }


        if (slug.equals("san-pham-moi") || slug.equals("khuyen-mai-hot") || slug.equals("top-ban-chay")) {
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
//        CompletableFuture<List<ProductResponseDTO>> productsFuture = CompletableFuture.supplyAsync(() -> productSearchRepository.searchByTitle(keyword, p, size));
        CompletableFuture<List<ProductResponseDTO>> productsFuture = CompletableFuture.supplyAsync(() ->
                productSearchRepository.searchByTitle(keyword, p, size)
                        .stream()
                        .sorted(Comparator.comparingInt(ProductResponseDTO::getPosition).reversed())
                        .collect(Collectors.toList())
        );

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

//    // Hàm Map danh sách ProductResponseDTO từ danh sách ProductEntity
//    public List<ProductResponseDTO> mapProductIndexResponsesDTO(List<ProductEntity> productEntities) {
//        List<ProductResponseDTO> productResponseDTOs = productMapper.productToProductResponsesDTO(productEntities);
//
//        IntStream.range(0, productEntities.size()).forEach(i -> {
//            ProductEntity product = productEntities.get(i);
//            ProductResponseDTO dto = productResponseDTOs.get(i);
//
//            // Map thương hiệu
//            if (product.getBrand() != null) {
//                ProductBrandResponse brandResponse = new ProductBrandResponse();
//                brandResponse.setId(product.getBrand().getId());
//                brandResponse.setTitle(product.getBrand().getTitle());
//                brandResponse.setSlug(product.getBrand().getSlug());
//                dto.setBrand(brandResponse);
//            }
//
////            if(product.getDiscount() != null) {
////                DiscountResponse discountResponse = new DiscountResponse();
////                discountResponse.getDiscountType();
////            }
//
////            if (product.getReviews() != null) {
////                List<ReviewResponse> reviewResponses = product.getReviews().stream()
////                        .filter(review -> review.getParent() == null) // Chỉ lấy root reviews
////                        .map(reviewService::convertToReviewResponse) // Chuyển đổi từng review
////                        .collect(Collectors.toList());
////
////                dto.setReviews(reviewResponses); // Gán vào DTO
////            }
//
//            // Map danh mục sản phẩm
//            if (product.getCategory() != null) {
//                dto.setCategory(product.getCategory().stream().map(category -> {
//                    ProductCategoryResponse categoryResponse = new ProductCategoryResponse();
//                    categoryResponse.setId(category.getId());
//                    categoryResponse.setTitle(category.getTitle());
//                    categoryResponse.setSlug(category.getSlug());
//
//                    if (category.getParent() != null) {
//                        ProductCategoryResponse parentCategoryResponse = new ProductCategoryResponse();
//                        parentCategoryResponse.setId(category.getParent().getId());
//                        parentCategoryResponse.setTitle(category.getParent().getTitle());
//                        parentCategoryResponse.setSlug(category.getParent().getSlug());
//                        categoryResponse.setParent(parentCategoryResponse);
//                    }
//
//
//                    return categoryResponse;
//                }).collect(Collectors.toList()));
//            }
//
//            // Map loại da của Product
//            if (product.getSkinTypes() != null) {
//                dto.setSkinTypes(product.getSkinTypes().stream()
//                        .map(skinType -> {
//                            SkinTypeResponse response = new SkinTypeResponse();
//                            response.setId(skinType.getId());
//                            response.setType(skinType.getType());
//                            return response;
//                        })
//                        .collect(Collectors.toList()));
//            }
//
//            // Map danh sách biến thể của Product
//            if (product.getVariants() != null) {
//                dto.setVariants(product.getVariants().stream()
//                        .map(variant -> {
//                            ProductVariantResponse variantResponse = new ProductVariantResponse();
//                            variantResponse.setId(variant.getId());
//                            variantResponse.setPrice(variant.getPrice());
//                            variantResponse.setVolume(variant.getVolume());
//                            variantResponse.setUnit(variant.getUnit());
//                            return variantResponse;
//                        })
//                        .collect(Collectors.toList()));
//            }
//
////
////            if(product.getDiscount() != null) {
////                dto.setD
////                dto.setDiscountPercent(product.getDiscountPercent());
////            }
//
//
//        });
//
//        return productResponseDTOs;
//    }



public List<ProductResponseDTO> mapProductIndexResponsesDTO(List<ProductEntity> productEntities) {
    return productEntities.stream()  // Sử dụng stream thay vì parallelStream
            .map(product -> {
                ProductResponseDTO dto = productMapper.productToProductResponseDTO(product);

                // Map thương hiệu
                if (product.getBrand() != null) {
                    ProductBrandResponse brandResponse = new ProductBrandResponse();
                    brandResponse.setId(product.getBrand().getId());
                    brandResponse.setTitle(product.getBrand().getTitle());
                    brandResponse.setSlug(product.getBrand().getSlug());
                    dto.setBrand(brandResponse);
                }

                // Map danh mục sản phẩm
                if (product.getCategory() != null) {
                    List<ProductCategoryResponse> categoryResponses = product.getCategory().stream()
                            .map(category -> {
                                ProductCategoryResponse categoryResponse = new ProductCategoryResponse();
                                categoryResponse.setId(category.getId());
                                categoryResponse.setTitle(category.getTitle());
                                categoryResponse.setSlug(category.getSlug());

                                // Map cha của danh mục (nếu có)
                                if (category.getParent() != null) {
                                    ProductCategoryResponse parentCategoryResponse = new ProductCategoryResponse();
                                    parentCategoryResponse.setId(category.getParent().getId());
                                    parentCategoryResponse.setTitle(category.getParent().getTitle());
                                    parentCategoryResponse.setSlug(category.getParent().getSlug());
                                    categoryResponse.setParent(parentCategoryResponse);
                                }

                                return categoryResponse;
                            }).collect(Collectors.toList());

                    dto.setCategory(categoryResponses);
                }

                // Map loại da
                if (product.getSkinTypes() != null) {
                    List<SkinTypeResponse> skinTypeResponses = product.getSkinTypes().stream()
                            .map(skinType -> {
                                SkinTypeResponse response = new SkinTypeResponse();
                                response.setId(skinType.getId());
                                response.setType(skinType.getType());
                                return response;
                            }).collect(Collectors.toList());

                    dto.setSkinTypes(skinTypeResponses);
                }

                // Map danh sách biến thể
                if (product.getVariants() != null) {
                    List<ProductVariantResponse> variantResponses = product.getVariants().stream()
                            .map(variant -> {
                                ProductVariantResponse variantResponse = new ProductVariantResponse();
                                variantResponse.setId(variant.getId());
                                variantResponse.setPrice(variant.getPrice());
                                variantResponse.setVolume(variant.getVolume());
                                variantResponse.setUnit(variant.getUnit());
                                return variantResponse;
                            }).collect(Collectors.toList());

                    dto.setVariants(variantResponses);
                }


                return dto;
            })
            .collect(Collectors.toList());
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


// Lấy top 5 sản phẩm bán chạy nhất theo loại da và thể loại
public List<ProductEntity> getTop5BestSellerProductBySkinTypeAndProductCategory(Long skinTypeId, String step) {
    String categoryKeyword = extractCategoryKeyword(step);
    if (categoryKeyword == null) {
        log.warn("No matching category found for step: {}", step);
        throw new AppException(ErrorCode.PRODUCT_CATEGORY_NOT_FOUND);
    }

    // Get current skin type
    SkinTypeEntity currentSkinType = skinTypeRepository.findById(skinTypeId)
            .orElseThrow(() -> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));
    String currentSkinTypeName = currentSkinType.getType();

    log.info("Searching top 5 selling products for skinType: {} with category: {}", skinTypeId, categoryKeyword);
    List<Long> productIds = productRepository.findTop5SellingProductsBySkinTypeAndCategory(
            skinTypeId,
            categoryKeyword
    );

    if (productIds.isEmpty()) {
        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }

    List<ProductEntity> products = productRepository.findAllById(productIds);

    // Filter products based on skin type in title
    return products.stream()
            .filter(product -> isMatchingSkinType(product.getTitle(), currentSkinTypeName))
            .collect(Collectors.toList());
}

// Lọc nội dung của tên danh mục sản phẩm, sau đó lấy những danh mục tương ứng với biến được khai trước đó và so sánh
private String extractCategoryKeyword(String step) {
    String stepLower = step.toLowerCase().trim();
    return CATEGORY_KEYWORDS.stream()
            .filter(keyword -> keyword.toLowerCase().equals(stepLower))
            .findFirst()
            .orElseGet(() -> {
                log.warn("Step '{}' did not match any category keywords", step);
                return null;
            });
}

// Lọc nội dung của bước làm da, sau đó lấy những keyword tương ứng với biến được khai trước đó và so sánh
    // Tránh những sản phẩm có tên khác với loại da hiện tại
private boolean isMatchingSkinType(String productTitle, String currentSkinType) {
    String titleLower = productTitle.toLowerCase();

    // If product title contains current skin type, return true
    if (titleLower.contains(currentSkinType.toLowerCase())) {
        return true;
    }

    // If product title contains any other skin type, return false
    return SKIN_TYPE_KEYWORDS.stream()
            .filter(skinType -> !skinType.equalsIgnoreCase(currentSkinType))
            .noneMatch(titleLower::contains);
    }
    //data dashboard
    public long countProduct() {
        return productRepository.countByStatusAndDeleted(Status.ACTIVE, false);
    }

    //top sản phẩm bán chạy

    public List<ProductResponseDTO> top10SellingProducts() {
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "position"));
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

    public Map<String, Object> top10SellingProductsDashBoard() {
        Pageable pageRequest = PageRequest.of(0, 10);
        List<Object[]> result = productRepository.findTop10SellingProductsDashBoard(pageRequest);

        // Sắp xếp theo soldQuantity giảm dần
        result.sort((a, b) -> Long.compare((Long) b[2], (Long) a[2]));

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : result) {
            Long productId = (Long) row[0];
            List<ProductVariantEntity> variants = productVariantRepository.findAllByProduct_Id(productId);

            Map<String, Object> productData = new HashMap<>();
            productData.put("id", productId);
            productData.put("title", (String) row[1]);
            productData.put("soldQuantity", (Long) row[2]);

            List<Map<String, Object>> variantData = new ArrayList<>();
            for (ProductVariantEntity variant : variants) {
                variantData.add(Map.of(
                        "id", variant.getId(),
                        "volume", variant.getVolume(),
                        "unit", variant.getUnit(),
                        "price", variant.getPrice()
                ));
            }
            productData.put("variants", variantData);

            data.add(productData);
        }

        return Map.of("data", data);
    }


//    //dashboard
//    //5 danh mục có nhiều sản phẩm nhất
//    public Map<String, Object> list5CategoryHaveTopProduct() {
//        List<Object[]> results = productCategoryRepository.findTop5CategoriesWithProductCount(PageRequest.of(0, 5));
//
//        return results.stream()
//                .collect(Collectors.toMap(
//                        result -> (String) result[0],   // categorytitle
//                        result -> ((Number) result[1]).intValue()  // productCount
//                ));
//    }
}


