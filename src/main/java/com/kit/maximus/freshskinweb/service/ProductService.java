package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
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

import java.util.*;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductService implements BaseService<ProductResponseDTO, CreateProductRequest, UpdateProductRequest, Long> {

    final ProductRepository productRepository;

    final ProductMapper productMapper;


    @Override
    public ProductResponseDTO add(CreateProductRequest request) {
        ProductEntity productEntity = productMapper.productToProductEntity(request);
        return productMapper.productToProductResponseDTO(productRepository.save(productEntity));
    }

    @Override
    public ProductResponseDTO update(Long id, UpdateProductRequest request) {
        ProductEntity listProduct = getProductEntityById(id);

        productMapper.updateProduct(listProduct, request);
        return productMapper.productToProductResponseDTO(productRepository.save(listProduct));
    }

    //thay doi status
    @Override
    public boolean update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        productRepository.findAllById(id)
                .forEach(productEntity -> {
                    productEntity.setStatus(statusEnum);
                    productRepository.save(productEntity);
                });
        return true;
    }

    /*
    Hàm này dùng để cập nhập position của 1 sản phẩm
     */
    public boolean update(Long id , int position) {
        ProductEntity Product = getProductEntityById(id);
             Product.setPosition(position);
             productRepository.save(Product);
        return true;
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
        productEntity.setStatus(Status.INACTIVE);
        productRepository.save(productEntity);
        return true;
    }

    /*
     Xóa(mềm) nhiều sản phẩm
     input: List<long> id
     output: boolean
   */
    @Override
    public boolean deleteTemporarily(List<Long> id) {
        productRepository.findAllByIdInAndStatus(id, Status.ACTIVE)
                .forEach(productEntity -> {
                    productEntity.setDeleted(true);
                    productEntity.setStatus(Status.INACTIVE);
                    productRepository.save(productEntity);
                });
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
        productEntity.setStatus(Status.ACTIVE);
        productRepository.save(productEntity);

        return true;
    }


    /*
  Phục hồi: nhiều sản phẩm
  - Khôi phục trạng thái sản phẩm(ACTIVE) và thay đổi DELETE(False)
  input: List<long> id
  output: boolean
*/
    @Override
    public boolean restore(List<Long> id) {
        List<ProductEntity> productEntities = productRepository.findAllByIdInAndStatus(id, Status.INACTIVE);

        productEntities.forEach(productEntity -> {
            productEntity.setDeleted(false);
            productEntity.setStatus(Status.INACTIVE);
            productRepository.save(productEntity);
        });
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
                productEntityPage = productRepository.findByTitleContainingIgnoreCase(keyword, pageable);
            } else {
                    // Tìm kiếm theo tên sản phẩm và status
                    Status statusEnum = getStatus(status);
                    productEntityPage = productRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, statusEnum, pageable);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                productEntityPage = productRepository.findAll(pageable);
            } else {
                    Status statusEnum = getStatus(status);
                    productEntityPage = productRepository.findAllByStatus(statusEnum, pageable);
            }
        }

        Page<ProductResponseDTO> list = productEntityPage.map(productMapper::productToProductResponseDTO);

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
}
