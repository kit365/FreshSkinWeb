package com.kit.maximus.freshskinweb.service.product;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductCategoryMapper;
import com.kit.maximus.freshskinweb.repository.ProductCategoryRepository;
import com.kit.maximus.freshskinweb.repository.search.ProductCategorySearchRepository;
import com.kit.maximus.freshskinweb.service.BaseService;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.kit.maximus.freshskinweb.specification.ProductCategorySpecification.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductCategoryService implements BaseService<ProductCategoryResponse, CreateProductCategoryRequest, UpdateProductCategoryRequest, Long> {

    ProductCategoryRepository productCategoryRepository;

    ProductCategoryMapper productCategoryMapper;

    ProductCategorySearchRepository productCategorySearchRepository;

    Cloudinary cloudinary;

    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public boolean add(CreateProductCategoryRequest request) {
        System.out.println(request);
        log.info("Request JSON: {}", request);

        ProductCategoryEntity productCategory = productCategoryMapper.productCategoryToProductEntity(request);

        if (request.getPosition() == null || request.getPosition() <= 0) {
            int size = productCategoryRepository.findAll().size();
            productCategory.setPosition(size + 1);
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            int count = 0;
            List<String> images = new ArrayList<>();
            try {
                for (MultipartFile file : request.getImage()) {
                    String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                    images.add(url);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }

            productCategory.setImage(images);
        }

        //nếu cha = null => đây là danh mục chinh
        ProductCategoryEntity parentCategory = productCategoryRepository.findById(request.getParentID()).orElse(null);
        productCategory.setParent(parentCategory);
        productCategory.setSlug(getSlug(request.getTitle()));

        //trường hợp tạo cha cùng lúc với con
        if (request.getChild() != null && !request.getChild().isEmpty()) {
            List<ProductCategoryEntity> children = productCategoryMapper.childCategoriesToEntity(request.getChild());

            for (ProductCategoryEntity child : children) {
                child.setParent(productCategory);
            }
            productCategory.setChild(children);
        }

        ProductCategoryResponse response = productCategoryMapper.productCategoryToProductCategoryResponseDTO(productCategoryRepository.save(productCategory));
        productCategorySearchRepository.indexProductCategory(response);
        return true;
    }

    public List<ProductCategoryResponse> getAll() {
        List<ProductCategoryEntity> list = productCategoryRepository.findAllByParentIsNull();
        return productCategoryMapper.toProductCateroiesResponseDTO(list);
    }


    public List<ProductCategoryResponse> getAlls() {
        // Lấy title, parent, child, id
        List<ProductCategoryEntity> list = productCategoryRepository.findAll();
        List<ProductCategoryResponse> responses = mapToCategoryResponse(list);
        responses.forEach(response -> {
            response.setSlug(getSlug(response.getTitle()));
            response.setDescription(null);
            response.setFeatured(null);
            response.setImage(null);
            response.setProducts(null);
            response.setChild(null);
        });
        return responses;
    }

    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public ProductCategoryResponse update(Long id, UpdateProductCategoryRequest request) {
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);


        if (StringUtils.hasLength(request.getStatus())) {
            productCategoryEntity.setStatus(getStatus(request.getStatus()));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            productCategoryEntity.setSlug(getSlug(request.getTitle()));
        }


        if (request.getParentID() != null) {
            ProductCategoryEntity parentCategory = productCategoryRepository.findById(request.getParentID()).orElse(null);
            productCategoryEntity.setParent(parentCategory);
        }

        if (request.getImage() != null) {
            productCategoryEntity.getImage().forEach(thumbnail -> {
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
            productCategoryEntity.setImage(newThumbnails);
        }

        productCategoryMapper.updateProductCategory(productCategoryEntity, request);
        return productCategoryMapper.productCategoryToProductCategoryResponseDTO(productCategoryRepository.save(productCategoryEntity));
    }

    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductCategoryEntity> productCategoryEntities = productCategoryRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productCategoryEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productCategoryRepository.saveAll(productCategoryEntities);
            return "Cập nhật trạng thái danh mục sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productCategoryEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productCategoryRepository.saveAll(productCategoryEntities);
            return "Xóa mềm danh mục sản phẩm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productCategoryEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productCategoryRepository.saveAll(productCategoryEntities);
            return "Phục hồi danh mục sản phẩm thành công";
        }
        return "Cập nhật danh mục sản phẩm thất bại";
    }


    //trước mắt về logic:
    //Khi xóa 1 danh mục cha => các danh mục khác sẽ mồ côi
    //khi xóa 1 danh mục có chứa product trong đó => hủy liên kết với product
    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public boolean delete(Long id) {
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);
        productCategorySearchRepository.delete(id);
        if (productCategoryEntity.getChild() != null) {
            productCategoryEntity.getChild().forEach(productCategoryEntity1 -> productCategoryEntity1.setParent(null));
            productCategoryRepository.saveAll(productCategoryEntity.getChild());
        }

        if (productCategoryEntity.getProducts() != null) {
            productCategoryEntity.getProducts().forEach(productEntity -> {
                productEntity.setCategory(null);
                productCategoryRepository.save(productCategoryEntity);
            });
        }

        if (productCategoryEntity.getImage() != null) {
            for (String url : productCategoryEntity.getImage()) {
                try {
                    deleteImageFromCloudinary(url);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        log.info("Delete: {}", id);
        productCategoryRepository.delete(productCategoryEntity);
        return true;
    }

    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public boolean delete(List<Long> id) {
        List<ProductCategoryEntity> list = productCategoryRepository.findAllById(id);

        for (ProductCategoryEntity productCategoryEntity : list) {
            productCategorySearchRepository.delete(productCategoryEntity.getId());
            if (productCategoryEntity.getChild() != null) {
                productCategoryEntity.getChild().forEach(productCategoryEntity1 -> productCategoryEntity1.setParent(null));
            }

            if (productCategoryEntity.getProducts() != null) {
                for (ProductEntity productEntity : productCategoryEntity.getProducts()) {
                    productEntity.setCategory(null);
                }
            }
        }

        for (ProductCategoryEntity productCategoryEntity : list) {
            if (productCategoryEntity.getImage() != null) {
                for (String img : productCategoryEntity.getImage()) {
                    try {
                        deleteImageFromCloudinary(img);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }

        }
        productCategoryRepository.saveAll(list);
        productCategoryRepository.deleteAll(list);
        return true;
    }

    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public boolean deleteTemporarily(Long id) {
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);
        productCategoryEntity.setDeleted(true);

        List<ProductEntity> products = productCategoryEntity.getProducts();
        for (ProductEntity productEntity : products) {
            productEntity.setStatus(Status.INACTIVE);
        }
        productCategoryRepository.save(productCategoryEntity);

        return true;
    }

    @CacheEvict(value = {"featuredProductCategory", "allCategory", "filteredCategories"}, allEntries = true)
    @Override
    public boolean restore(Long id) {
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);
        productCategoryEntity.setDeleted(false);

        List<ProductEntity> products = productCategoryEntity.getProducts();
        for (ProductEntity productEntity : products) {
            productEntity.setStatus(Status.ACTIVE);
        }
        productCategoryRepository.save(productCategoryEntity);
        return true;
    }

    @Override
    public ProductCategoryResponse showDetail(Long id) {
        //Khi trả th không trả Child mà chỉ trả parentID => PaerenTitle
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);

        ProductCategoryResponse productCategoryResponse = productCategoryMapper.productCategoryToProductCategoryResponseDTO(productCategoryEntity);

        if (productCategoryEntity.getParent() != null) {
            ProductCategoryEntity parent = productCategoryEntity.getParent();

            ProductCategoryResponse parentID = new ProductCategoryResponse();
            parentID.setTitle(parent.getTitle());

            productCategoryResponse.setParent(parentID);

        }


        productCategoryResponse.setChild(null);
//        if(productCategoryEntity.getParent() != null) {
//            String parentTitle = productCategoryEntity.getParent().getTitle();
//            productCategoryResponse.getParent().setTitle(parentTitle);
//        }

        return productCategoryResponse;
    }

    @Cacheable(value = "allCategory")
    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> showALL() {
        List<ProductCategoryEntity> list = productCategoryRepository.findAll();
        List<ProductCategoryResponse> result = new ArrayList<>();
        list.forEach(productCategoryEntity -> {
            ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();
            productCategoryResponse.setId(productCategoryEntity.getId());
            productCategoryResponse.setTitle(productCategoryEntity.getTitle());
            productCategoryResponse.setSlug(productCategoryEntity.getSlug());
            result.add(productCategoryResponse);
        });
        return result;
    }

    public Map<String, Object> showDetaill(Long id) {
        //Khi trả th không trả Child mà chỉ trả parentID => PaerenTitle
        Map<String, Object> map = new HashMap<>();
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);

//        ProductCategoryResponse productCategoryResponse = new ProductCategoryResponse();


        ProductCategoryResponse productCategoryResponse = productCategoryMapper.productCategoryToProductCategoryResponseDTO(productCategoryEntity);
        productCategoryResponse.setChild(null);
        String title = productCategoryEntity.getParent().getTitle();
        map.put("productCategoryResponse", productCategoryResponse);
        map.put("titleParent", title);


//        response.setProductIDs(getProductIDs(productCategoryEntity));
//
//        productCategoryEntity.getChild().forEach(childCategoryEntity -> {
//            ProductCategoryResponse parentCategoryResponse = new ProductCategoryResponse();
//            parentCategoryResponse.setTitle(childCategoryEntity.getTitle());

//        });

        return map;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ProductCategoryEntity> productCategoryEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                productCategoryEntities = productCategoryRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                productCategoryEntities = productCategoryRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productCategoryEntities = productCategoryRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                productCategoryEntities = productCategoryRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<ProductCategoryResponse> list = productCategoryEntities.map(productCategoryMapper::productCategoryToProductCategoryResponseDTO);

//        if (!list.hasContent()) {
//            return null;
//        }

        map.put("product_category", list.getContent());
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

        Page<ProductCategoryEntity> productCategoryEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                productCategoryEntities = productCategoryRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, true, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                productCategoryEntities = productCategoryRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, true);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productCategoryEntities = productCategoryRepository.findAllByDeleted(true, pageable);
            } else {
                Status statusEnum = getStatus(status);
                productCategoryEntities = productCategoryRepository.findAllByStatusAndDeleted(statusEnum, true, pageable);
            }
        }

        Page<ProductCategoryResponse> list = productCategoryEntities.map(productCategoryMapper::productCategoryToProductCategoryResponseDTO);

//        if (!list.hasContent()) {
//            return null;
//        }

        map.put("product_category", list.getContent());
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
    private ProductCategoryEntity getCategoryById(Long id) {
        return productCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_CATEGORY_NOT_FOUND));
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
                "folder", "product-category",
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


    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        String publicId = temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
        System.out.println(publicId);
        return publicId;
    }


    private List<Long> getProductIDs(ProductCategoryEntity productCategoryEntity) {
        List<Long> idProduct = new ArrayList<>();
        productCategoryEntity.getProducts().forEach(productEntity -> {
            idProduct.add(productEntity.getId());
        });
        return idProduct;
    }

    /*
    HOME
     */
    //Hàm này dùng để lấy ra top 8 danh mục nổi bật(Bao gồm chứa Product)

    @Cacheable("featuredProductCategory")
    public List<ProductCategoryResponse> getFeaturedProductCategories() {
        List<ProductCategoryEntity> categories = productCategoryRepository.findTop8ByStatusAndDeletedAndFeatured(
                Status.ACTIVE, false, true, Sort.by(Sort.Direction.DESC, "position")
        );


//        categories.forEach(productCategoryEntity -> {
//            productCategoryEntity.getProducts().removeIf(productEntity ->
//            {
//                boolean b = productEntity.isDeleted() || productEntity.getStatus() != Status.ACTIVE;
//                return b;
//            });
//        });

        List<ProductCategoryResponse> productCategoryFeature = mapToCategoryResponse(categories);


        productCategoryFeature.forEach(category -> {
            if (category.getProducts() != null) {
                category.getProducts().removeIf(product ->
                        Boolean.TRUE.equals(product.isDeleted()) || getStatus(product.getStatus()) == Status.INACTIVE
                );

                category.getProducts().forEach(product -> product.setDescription(null));
            }
        });
        return productCategoryFeature;
    }

    @Transactional(readOnly = true)
    @Cacheable("filteredCategories")
    //Hàm này dùng để lấy ra n danh mục tùy chọn, số lượng n sản phẩm
    public List<ProductCategoryResponse> getFilteredCategories(List<String> titles, int limit) {
        List<ProductCategoryResponse> result = new ArrayList<>();

        Specification<ProductCategoryEntity> specification = findCategoryByTitle(titles)
                .and(filterByStatus(Status.ACTIVE))
                .and(isNotDeleted());

        List<ProductCategoryEntity> categories = productCategoryRepository.findAll(specification);

        result = mapToCategoryResponse(categories);

        result.removeIf(category ->
                category.getProducts().stream()
                        .anyMatch(product -> product.getStatus().equalsIgnoreCase("INACTIVE") || product.isDeleted())
        );

        result.forEach(category -> {
            category.setDescription(null);

            List<ProductResponseDTO> limitedProducts = category.getProducts()
                    .stream()
                    .sorted(Comparator.comparing(ProductResponseDTO::getPosition,
                            Comparator.nullsLast(Comparator.reverseOrder()))) //nhung thang null se nam o cuoi
                    .limit(limit)
                    .collect(Collectors.toList());

            category.setProducts(limitedProducts);
            limitedProducts.forEach(product -> {
                product.setDescription(null);
                product.setSkinTypes(null);
                product.setIngredients(null);
                product.setOrigin(null);
                product.setSkinIssues(null);
                product.setUsageInstructions(null);
            });
        });

        return result;
    }


    //Hàm này dùng để map thủ công danh sách danh mục sản phẩm
    protected List<ProductCategoryResponse> mapToCategoryResponse(List<ProductCategoryEntity> categories) {
        List<ProductCategoryResponse> categoryResponses = new ArrayList<>();
        List<ProductCategoryResponse> children = new ArrayList<>();
        categories.forEach(productCategoryEntity -> {
            ProductCategoryResponse response = new ProductCategoryResponse();
            response.setId(productCategoryEntity.getId());
            response.setFeatured(productCategoryEntity.isFeatured());
            response.setTitle(productCategoryEntity.getTitle());
            response.setSlug(productCategoryEntity.getSlug());
            response.setDescription(productCategoryEntity.getDescription());
            response.setImage(productCategoryEntity.getImage());
            if (productCategoryEntity.getChild() != null) {
                productCategoryEntity.getChild().forEach(child -> {
//                    if(child.getProducts() != null) {
//                        child.getProducts().forEach(product -> {
//                            ProductCategoryResponse childResponse = new ProductCategoryResponse();
//                            childResponse.setId(product.getId());
//                            childResponse.setTitle(product.getTitle());
//                        });
//                    }
                    ProductCategoryResponse responseChild = new ProductCategoryResponse();
                    responseChild.setId(child.getId());
                    responseChild.setTitle(child.getTitle());
                    children.add(responseChild);
                });
                response.setChild(children);
            }

            // Tạo danh sách riêng cho từng danh mục
            List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();

            productCategoryEntity.getProducts().forEach(productEntity -> {
                ProductResponseDTO productResponseDTO = new ProductResponseDTO();
                productResponseDTO.setId(productEntity.getId());
                productResponseDTO.setTitle(productEntity.getTitle());
                productResponseDTO.setSlug(productEntity.getSlug());
                productResponseDTO.setDescription(productEntity.getDescription());
                productResponseDTO.setThumbnail(productEntity.getThumbnail());
                productResponseDTO.setDiscountPercent(productEntity.getDiscountPercent());
                productResponseDTO.setFeatured(productEntity.getFeatured());
                productResponseDTO.setStatus(String.valueOf(productEntity.getStatus()));
                productResponseDTO.setDeleted(productEntity.isDeleted());

                if (productEntity.getBrand() != null) {
                    ProductBrandResponse brandResponse = new ProductBrandResponse();
                    brandResponse.setId(productEntity.getBrand().getId());
                    brandResponse.setTitle(productEntity.getBrand().getTitle());
                    productResponseDTO.setBrand(brandResponse);
                }

                if (productEntity.getSkinTypes() != null) {
                    List<SkinTypeResponse> skinTypeResponses = new ArrayList<>();
                    productEntity.getSkinTypes().forEach(skinTypeEntity -> {
                        SkinTypeResponse skinTypeResponse = new SkinTypeResponse();
                        skinTypeResponse.setId(skinTypeEntity.getId());
                        skinTypeResponse.setType(skinTypeEntity.getType());
                        skinTypeResponses.add(skinTypeResponse);
                    });
                    productResponseDTO.setSkinTypes(skinTypeResponses);
                }

                // Tạo danh sách riêng cho từng sản phẩm
                List<ProductVariantResponse> variantResponses = new ArrayList<>();

                productEntity.getVariants().forEach(productVariantEntity -> {
                    ProductVariantResponse productVariantResponse = new ProductVariantResponse();
                    productVariantResponse.setPrice(productVariantEntity.getPrice());
                    productVariantResponse.setId(productVariantEntity.getId());
                    productVariantResponse.setVolume(productVariantEntity.getVolume());
                    productVariantResponse.setUnit(productVariantEntity.getUnit());
                    variantResponses.add(productVariantResponse);
                });

                productResponseDTO.setVariants(variantResponses);
                productResponseDTOS.add(productResponseDTO);
            });

            response.setProducts(productResponseDTOS);
            categoryResponses.add(response);
        });

        return categoryResponses;
    }

    //Hàm này dùng để map thủ công 1 ProductCategory
    private ProductCategoryResponse mapToCategoryResponse(ProductCategoryEntity productCategoryEntity) {

        ProductCategoryResponse response = new ProductCategoryResponse();
        response.setId(productCategoryEntity.getId());
        response.setFeatured(productCategoryEntity.isFeatured());
        response.setTitle(productCategoryEntity.getTitle());
        response.setSlug(productCategoryEntity.getSlug());
        response.setDescription(productCategoryEntity.getDescription());
        response.setImage(productCategoryEntity.getImage());


        // Tạo danh sách riêng cho từng danh mục
        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();

        productCategoryEntity.getProducts().forEach(productEntity -> {

            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setId(productEntity.getId());
            productResponseDTO.setTitle(productEntity.getTitle());
            productResponseDTO.setSlug(productEntity.getSlug());
            productResponseDTO.setDescription(productEntity.getDescription());
            productResponseDTO.setThumbnail(productEntity.getThumbnail());
            productResponseDTO.setDiscountPercent(productEntity.getDiscountPercent());
            productResponseDTO.setFeatured(productEntity.getFeatured());

            ProductBrandResponse productBrandResponse = new ProductBrandResponse();
            productBrandResponse.setTitle(productEntity.getBrand().getTitle());
            productResponseDTO.setBrand(productBrandResponse);

            // Tạo danh sách riêng cho từng sản phẩm
            List<ProductVariantResponse> variantResponses = new ArrayList<>();

            productEntity.getVariants().forEach(productVariantEntity -> {
                ProductVariantResponse productVariantResponse = new ProductVariantResponse();
                productVariantResponse.setPrice(productVariantEntity.getPrice());
                variantResponses.add(productVariantResponse);
            });

            productResponseDTO.setVariants(variantResponses);
            productResponseDTOS.add(productResponseDTO);
        });

        response.setProducts(productResponseDTOS);


        return response;
    }


    public boolean indexProductCategory() {
        List<ProductCategoryEntity> productCategoryEntities = productCategoryRepository.findAll();
        List<ProductCategoryResponse> responseDTOS = productCategoryMapper.toProductCateroiesResponseDTO(productCategoryEntities);
        responseDTOS.forEach(productCategorySearchRepository::indexProductCategory);
        return false;
    }

    public Map<String, Object> list5CategoryHaveTopProduct() {
        List<Object[]> results = productCategoryRepository.findTop5CategoriesWithProductCount(PageRequest.of(0, 5));

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            data.add(Map.of(
                    "title", (String) row[0],
                    "total", ((Number) row[1]).intValue()
            ));
        }

        return Map.of("data", data);
    }

}