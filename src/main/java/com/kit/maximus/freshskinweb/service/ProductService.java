package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductBrandEntity;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.ProductBrandRepository;
import com.kit.maximus.freshskinweb.repository.ProductCategoryRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
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

import java.text.Normalizer;
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

    @Override
    public ProductResponseDTO add(CreateProductRequest request) {
        ProductCategoryEntity productCategoryEntity = productCategoryRepository.findById(request.getCategoryId()).orElse(null);
        ProductBrandEntity productBrandEntity = productBrandRepository.findById(request.getBrandId()).orElse(null);
        ProductBrandEntity r = productBrandRepository.findById(request.getBrandId()).orElse(null);
        ProductEntity productEntity = productMapper.productToProductEntity(request);

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

        return productMapper.productToProductResponseDTO(productRepository.save(productEntity));
    }


    @Override
    public ProductResponseDTO update(Long id, UpdateProductRequest request) {
        if (StringUtils.hasLength(request.getStatus())) {
            request.setStatus(request.getStatus().toUpperCase());
            getStatus(request.getStatus());
        }
        ProductEntity listProduct = getProductEntityById(id);

        if (StringUtils.hasLength(request.getTitle())) {
            listProduct.setSlug(getSlug(request.getTitle()));
        }

        //BO SUNG BAN LOI KHONG TIM THAY ID DANH MUC SAN PHAM
        if (request.getCategoryId() > 0) {
            ProductCategoryEntity productCategoryEntity = productCategoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_CATEGORY_NOT_FOUND));
            listProduct.setCategory(productCategoryEntity);
        }

        if (request.getBrandId() > 0) {
            ProductBrandEntity productBrandEntity = productBrandRepository.findById(request.getBrandId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_BRAND_NOT_FOUND));
            listProduct.setBrand(productBrandEntity);
        }

        if (request.getVariants() != null) {

            for (ProductVariantEntity requestedVariant : request.getVariants()) {
                if (requestedVariant.getId() == null) {
                    boolean checkDuplicateVolume = checkDuplicateVolume(request.getVariants(), listProduct.getId());
                    ProductVariantEntity newVariant = new ProductVariantEntity();
                    newVariant.setVolume(requestedVariant.getVolume());
                    newVariant.setPrice(requestedVariant.getPrice());
                    newVariant.setProduct(listProduct);
                    listProduct.createProductVariant(newVariant);
                } else {
                    for (ProductVariantEntity updatedVariant : listProduct.getVariants()) {
                        if (requestedVariant.getId().equals(updatedVariant.getId())) {
                            updatedVariant.setVolume(requestedVariant.getVolume());
                            updatedVariant.setPrice(requestedVariant.getPrice());

                        }
                    }
                }
            }
        }

        productMapper.updateProduct(listProduct, request);
        return productMapper.productToProductResponseDTO(productRepository.save(listProduct));
    }


    //thay doi thanh String de quan lý message
    @Override
    public boolean update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductEntity> productEntities = productRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productRepository.saveAll(productEntities);
//            return "Cập nhật trạng thái sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productRepository.saveAll(productEntities);
//            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productRepository.saveAll(productEntities);
//            return "Phục hồi thành công";
        }
//        return "Cập nhật thất bại";
        return true;
    }

    @Override
    public UserResponseDTO addOrder(Long id, CreateUserRequest request) {
        return null;
    }


    /*
       Xóa(cứng) 1 sản phẩm
       input: long id
       output: boolean
     */

    @Override
    public boolean delete(Long id) {
        ProductEntity productEntity = getProductEntityById(id);

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


    //xóa product_variant
    public boolean deleteProductVariants(Long id, ProductVariantEntity productVariantEntities) {
        ProductEntity productEntity = getProductEntityById(id);

        for (ProductVariantEntity request : productEntity.getVariants()) {
            if (request.getId().equals(productVariantEntities.getId())) {
                productEntity.removeProductVariant(productVariantEntities);
                return true;
            }
        }
        return false;
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

    @Override
    public UserResponseDTO addOrder(Long id, CreateOrderRequest request) {
        return null;
    }

    //-------------------------------------------------------------------------------------------------------------
    /*
 Phục hồi: nhiều sản phẩm
 - Khôi phục trạng thái sản phẩm(ACTIVE) và thay đổi DELETE(False)
 input: List<long> id
 output: boolean
*/
    @Override
    public boolean restore(List<Long> id) {
//        List<ProductEntity> productEntities = productRepository.findAllByIdInAndStatus(id, Status.ACTIVE);
//
//        productEntities.forEach(productEntity -> {
//            productEntity.setDeleted(false);
//            productRepository.save(productEntity);
//        });
        return true;
    }


    //    //thay doi status
//    @Override
//    public boolean update(List<Long> id, String status) {
//
//        Status statusEnum = getStatus(status);
//        productRepository.findAllById(id)
//                .forEach(productEntity -> {
//                    productEntity.setStatus(statusEnum);
//                    productRepository.save(productEntity);
//                });
//        return true;
//    }


    /*
 Xóa(mềm) nhiều sản phẩm
 input: List<long> id
 output: boolean
*/
    @Override
    public boolean deleteTemporarily(List<Long> id) {
//        productRepository.findAllByIdInAndStatus(id, Status.ACTIVE)
//                .forEach(productEntity -> {
//                    productEntity.setDeleted(true);
//                    productRepository.save(productEntity);
//                });
        return true;
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

    private boolean checkDuplicateVolume(List<ProductVariantEntity> entityList, Long productId) {
        Map<Integer, ProductVariantEntity> volumeMap = new HashMap<>();

        ProductEntity listProduct = getProductEntityById(productId);
        List<ProductVariantEntity> productVariantEntities = listProduct.getVariants();

        for (ProductVariantEntity productVariantEntity : productVariantEntities) {
            volumeMap.put(productVariantEntity.getVolume(), productVariantEntity);
        }

        for (ProductVariantEntity productVariantEntity : entityList) {
            if (volumeMap.containsKey(productVariantEntity.getVolume())) {
                throw new AppException(ErrorCode.VOLUME_EXISTED);
            }
        }
        return true;
    }

}
