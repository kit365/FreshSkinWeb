package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductCategoryMapper;
import com.kit.maximus.freshskinweb.repository.ProductCategoryRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductCategoryService implements BaseService<ProductCategoryResponse, CreateProductCategoryRequest, UpdateProductCategoryRequest, Long> {

    ProductCategoryRepository productCategoryRepository;

    ProductCategoryMapper productCategoryMapper;

    @Override
    public ProductCategoryResponse add(CreateProductCategoryRequest request) {
        log.info("Request JSON: {}", request);

        ProductCategoryEntity productCategoryEntity = productCategoryMapper.productCategoryToProductEntity(request);


        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = productCategoryRepository.findAll().size();
            productCategoryEntity.setPosition(size + 1);
        }

        productCategoryEntity.setSlug(getSlug(request.getTitle()));
        return productCategoryMapper.productCategoryToProductCategoryResponseDTO(productCategoryRepository.save(productCategoryEntity));
    }

    public List<ProductCategoryResponse> getAll() {
        return productCategoryMapper.toProductCateroiesResponseDTO(productCategoryRepository.findAll());
    }

    @Override
    public ProductCategoryResponse update(Long id, UpdateProductCategoryRequest request) {
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);

        if (StringUtils.hasLength(request.getStatus())) {
            productCategoryEntity.setStatus(getStatus(request.getStatus()));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            productCategoryEntity.setSlug(getSlug(request.getTitle()));
        }


        productCategoryMapper.updateProductCategory(productCategoryEntity, request);
        return productCategoryMapper.productCategoryToProductCategoryResponseDTO(productCategoryRepository.save(productCategoryEntity));
    }

    @Override
    public boolean update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductCategoryEntity> productCategoryEntities = productCategoryRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productCategoryEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productCategoryRepository.saveAll(productCategoryEntities);
//            return "Cập nhật trạng thái danh mục sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productCategoryEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productCategoryRepository.saveAll(productCategoryEntities);
//            return "Xóa mềm danh mục sản phẩm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productCategoryEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productCategoryRepository.saveAll(productCategoryEntities);
//            return "Phục hồi danh mục sản phẩm thành công";
        }
//        return "Cập nhật danh mục sản phẩm thất bại";
        return true;
    }

    @Override
    public UserResponseDTO addOrder(Long id, CreateUserRequest request) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        ProductCategoryEntity productCategoryEntity = getCategoryById(id);
        log.info("Delete: {}", id);
        productCategoryRepository.delete(productCategoryEntity);
        return true;
    }

    @Override
    public boolean delete(List<Long> id) {
        List<ProductCategoryEntity> list = productCategoryRepository.findAllById(id);
        productCategoryRepository.deleteAll(list);
        return true;
    }

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

    @Override
    public boolean deleteTemporarily(List<Long> id) {
//        productCategoryRepository.findAllById(id).forEach(productCategoryEntity -> {
//            productCategoryEntity.setDeleted(true);
//
//            List<ProductEntity> products = productCategoryEntity.getProducts();
//            for (ProductEntity productEntity : products) {
//                productEntity.setStatus(Status.INACTIVE);
//            }
//
//
//            productCategoryRepository.save(productCategoryEntity);
//        });
        return true;
    }

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
    public boolean restore(List<Long> id) {
//        productCategoryRepository.findAllById(id).forEach(productCategoryEntity -> {
//            productCategoryEntity.setDeleted(false);
//
//            List<ProductEntity> products = productCategoryEntity.getProducts();
//            for (ProductEntity productEntity : products) {
//                productEntity.setStatus(Status.ACTIVE);
//            }
//
//            productCategoryRepository.save(productCategoryEntity);
//        });
        return true;
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

    @Override
    public UserResponseDTO addOrder(Long id, CreateOrderRequest request) {
        return null;
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

//    //chuyen String thanh Feature
//    private boolean isFeature(String feature) {
//        try {
//            return Boolean.parseBoolean(feature);
//        } catch (IllegalArgumentException e) {
//            log.warn("Invalid feature provided: '{}'", feature);
//            throw new AppException(ErrorCode.KEY_INVALID);
//        }
//    }

//    private Boolean convertToBoolean(String feature) {
//        if (feature == null) {
//            return false;
//        }
//        if (feature.equalsIgnoreCase("true")) {
//            return true;
//        } else if (feature.equalsIgnoreCase("false")) {
//            return false;
//        } else {
//            log.warn("Invalid feature provided: '{}'", feature);
//            throw new AppException(ErrorCode.KEY_INVALID);
//        }
//    }


}
