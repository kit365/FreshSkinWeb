package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {


//    @Mapping(target = "category", ignore = true)
    ProductCategoryEntity productCategoryToProductEntity(CreateProductCategoryRequest request);

    ProductCategoryResponse productCategoryToProductCategoryResponseDTO(ProductCategoryEntity request);

    List<   ProductCategoryResponse > toProductCateroiesResponseDTO(List<ProductCategoryEntity> request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductCategory(@MappingTarget ProductCategoryEntity productCategoryEntity, UpdateProductCategoryRequest request);
}
