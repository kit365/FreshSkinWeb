package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.product_brand.CreateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.request.product_brand.UpdateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductBrandResponse;
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
public class ProductBrandService implements BaseService<ProductBrandResponse, CreateProductBrandRequest, UpdateProductBrandRequest, Long> {

    ProductBrandRepository productBrandRepository;

    ProductBrandMapper productBrandMapper;

    @Override
    public ProductBrandResponse add(CreateProductBrandRequest request) {
        log.info("Request JSON: {}", request);

        ProductBrandEntity productBrandEntity = productBrandMapper.productBrandToProductEntity(request);

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = productBrandRepository.findAll().size();
            productBrandEntity.setPosition(size + 1);
        }

        productBrandEntity.setSlug(getSlug(request.getTitle()));
        return productBrandMapper.productBrandToProductBrandResponseDTO(productBrandRepository.save(productBrandEntity));
    }

    public List<ProductBrandResponse> getAll() {
        return productBrandMapper.toProductBrandsResponseDTO(productBrandRepository.findAll());
    }

    @Override
    public boolean update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<ProductBrandEntity> productBrandEntities = productBrandRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            productBrandEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            productBrandRepository.saveAll(productBrandEntities);
//            return "Cập nhật trạng thái thương hiệu sản phẩm thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            productBrandEntities.forEach(productEntity -> productEntity.setDeleted(true));
            productBrandRepository.saveAll(productBrandEntities);
//            return "Xóa mềm thương hiệu sản phẩm thành công";
        } else if (statusEnum == Status.RESTORED) {
            productBrandEntities.forEach(productEntity -> productEntity.setDeleted(false));
            productBrandRepository.saveAll(productBrandEntities);
//            return "Phục hồi thương hiệu sản phẩm thành công";
        }
//        return "Cập nhật thương hiệu sản phẩm thất bại";
        return true;
    }

    @Override
    public ProductBrandResponse update(Long id, UpdateProductBrandRequest request) {
        ProductBrandEntity brandEntity = getBrandById(id);


        if (StringUtils.hasLength(request.getStatus())) {
            brandEntity.setStatus(getStatus(request.getStatus()));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            brandEntity.setSlug(getSlug(request.getTitle()));
        }

        productBrandMapper.updateProductBrand(brandEntity, request);
        return productBrandMapper.productBrandToProductBrandResponseDTO(productBrandRepository.save(brandEntity));
    }

    @Override
    public boolean delete(Long id) {
        ProductBrandEntity brandEntity = getBrandById(id);
        log.info("Delete: {}", id);
        productBrandRepository.delete(brandEntity);
        return true;
    }

    @Override
    public boolean delete(List<Long> id) {
        List<ProductBrandEntity> list = productBrandRepository.findAllById(id);
        productBrandRepository.deleteAll(list);
        return true;
    }

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

        map.put("product_brand", list.getContent());
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


    //--------------------------------------------------------------------------------------------------------------
    //    @Override
//    public boolean update(List<Long> id, String status) {
//        Status statusEnum = getStatus(status);
//
//        productBrandRepository.findAllById(id).forEach(brandEntity -> {
//            brandEntity.setStatus(statusEnum);
//            productBrandRepository.save(brandEntity);
//        });
//
//        return true;
//    }
    @Override
    public boolean restore(List<Long> id) {
//        productBrandRepository.findAllById(id).forEach(productBrand -> {
//            productBrand.setDeleted(false);
//
//            List<ProductEntity> products = productBrand.getProducts();
//            for (ProductEntity productEntity : products) {
//                productEntity.setStatus(Status.ACTIVE);
//            }
//
//            productBrandRepository.save(productBrand);
//        });
        return true;
    }

    @Override
    public boolean deleteTemporarily(List<Long> id) {
//        productBrandRepository.findAllById(id).forEach(brandEntity -> {
//            brandEntity.setDeleted(true);
//
//            List<ProductEntity> products = brandEntity.getProducts();
//            for (ProductEntity productEntity : products) {
//                productEntity.setStatus(Status.INACTIVE);
//            }
//
//            productBrandRepository.save(brandEntity);
//        });
        return true;
    }
}

