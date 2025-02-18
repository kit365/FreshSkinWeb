package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.mapper.ProductCategoryMapper;
import com.kit.maximus.freshskinweb.repository.ProductCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductCategoryService implements BaseService<ProductCategoryResponse, CreateProductCategoryRequest, UpdateProductCategoryRequest,Long> {

    ProductCategoryRepository productCategoryRepository;

    ProductCategoryMapper productCategoryMapper;

    @Override
    public ProductCategoryResponse add(CreateProductCategoryRequest request) {
        ProductCategoryEntity productCategoryEntity = productCategoryMapper.productCategoryToProductEntity(request);

        if(request.getPosition() <= 0){
            int size  = productCategoryRepository.findAll().size();
            productCategoryEntity.setPosition(size + 1);
        }

        productCategoryEntity.setSlug(getSlug(request.getTitle()));


        return productCategoryMapper.productCategoryToProductResponseDTO(productCategoryRepository.save(productCategoryEntity));

    }

    @Override
    public ProductCategoryResponse update(Long aLong, UpdateProductCategoryRequest request) {
        return null;
    }

    @Override
    public boolean update(List<Long> id, String status) {
        return false;
    }

    @Override
    public boolean delete(Long aLong) {
        return false;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long aLong) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(List<Long> longs) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        return false;
    }

    @Override
    public boolean restore(List<Long> longs) {
        return false;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }


    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }
}
