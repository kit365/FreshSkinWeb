package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.ProductRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductService implements BaseService<ProductRequestDTO, ProductResponseDTO> {

    final ProductRepository productRepository;

    final ProductMapper productMapper;

    @Override
    public ProductResponseDTO add(ProductRequestDTO productRequestDTO) {
        ProductEntity productEntity = productMapper.productToProductEntity(productRequestDTO);
        return productMapper.productToProductResponseDTO(productRepository.save(productEntity));
    }

    @Override
    public boolean delete(Long id) {
        ProductEntity productEntity = getProductEntityById(id);
        if (productEntity == null) {
            log.info("User {} not exist", id);
            return false;
        }
        log.info("Delete: {}", id);
        productRepository.delete(productEntity);
        return true;
    }

    @Override
    public boolean delete(List<Long> id) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long id) {
        ProductEntity productEntity = getProductEntityById(id);
        if (productEntity == null) {
            log.info("User {} not exist", id);
            return false;
        }
        log.info("Delete temporarily: {}", id);
        productEntity.setDeleted(true);
        productEntity.setStatus(Status.INACTIVE);
        productRepository.save(productEntity);
        return true;
    }

    @Override
    public boolean deleteTemporarily(List<Long> id) {
        List<ProductEntity> productEntityList
                = id.stream()
                .map(this::getProductEntityById)
                .filter(Objects::nonNull)
                .peek(productEntity -> {
                    log.info("Delete temporarily: {}", productEntity.getId());
                    productEntity.setDeleted(true);
                    productEntity.setStatus(Status.INACTIVE);
                })
                .toList();
        productRepository.saveAll(productEntityList);
        return true;
    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO productRequestDTO) {
        ProductEntity listProduct = getProductEntityById(id);

        if (listProduct == null) {
            log.info("User {} not exist", id);
            return null;
        }


        productMapper.updateProduct(listProduct, productRequestDTO);
        return productMapper.productToProductResponseDTO(productRepository.save(listProduct));
    }

    @Override
    public List<ProductResponseDTO> update(List<ProductRequestDTO> listRequest) {
        return List.of();
    }


    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection) {
        Map<String, Object> map = new HashMap<>();

        if(!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;


        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;

        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ProductEntity> productEntitiPage = productRepository.findAll(pageable);

        Page<ProductResponseDTO> list = productEntitiPage.map(productMapper::productToProductResponseDTO);

        if (!list.hasContent()) {
            return null;
        }

        map.put("products", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    private ProductEntity getProductEntityById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
