package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.productvariant.CreateProductVariant;
import com.kit.maximus.freshskinweb.dto.request.productvariant.UpdateProductVariant;
import com.kit.maximus.freshskinweb.dto.response.ProductVariantResponse;
import com.kit.maximus.freshskinweb.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductVariantService implements BaseService<ProductVariantResponse, CreateProductVariant, UpdateProductVariant,Long>{

    ProductVariantRepository productVariantRepository;


    @Override
    public ProductVariantResponse add(CreateProductVariant request) {
        return null;
    }

    @Override
    public ProductVariantResponse update(Long aLong, UpdateProductVariant request) {
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
}
