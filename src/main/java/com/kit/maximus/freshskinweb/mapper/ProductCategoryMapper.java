package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {


//    @Mapping(target = "category", ignore = true)
    ProductCategoryEntity productCategoryToProductEntity(CreateProductCategoryRequest productRequest);

    ProductCategoryResponse productCategoryToProductResponseDTO(ProductCategoryEntity product);

//    List<ProductResponseDTO> toUserResponseDTO(List<ProductEntity> productEntities);

    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductCategory(@MappingTarget ProductCategoryEntity productCategoryEntity, UpdateProductCategoryRequest productCategoryRequestDTO);
}
