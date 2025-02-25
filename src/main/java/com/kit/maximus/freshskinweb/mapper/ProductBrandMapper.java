package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.product_brand.CreateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.request.product_brand.UpdateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductBrandResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.ProductBrandEntity;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductBrandMapper {

    @Mapping(target = "image", ignore = true)
    ProductBrandEntity productBrandToProductBrandEntity(CreateProductBrandRequest request);

    @Mapping(target = "productIDs", ignore = true)
    ProductBrandResponse productBrandToProductBrandResponseDTO(ProductBrandEntity request);

    @Mapping(target = "productIDs", ignore = true)
    List<ProductBrandResponse> toProductBrandsResponseDTO(List<ProductBrandEntity> request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductBrand(@MappingTarget ProductBrandEntity productBrandEntity, UpdateProductBrandRequest request);
}
