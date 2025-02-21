package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.productcategory.ChildCategoryDTO;
import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.ParentCategoryDTO;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "child", ignore = true)
    ProductCategoryEntity productCategoryToProductEntity(CreateProductCategoryRequest request);


    @Mapping(target = "child", ignore = true)
    ProductCategoryEntity childCategoryToEntity(ChildCategoryDTO request);

    @Mapping(target = "child", ignore = true)
    List<ProductCategoryEntity> childCategoriesToEntity(List<ChildCategoryDTO> request);
//    ProductCategoryEntity parentCategoriesToEntity(ParentCategoryDTO request);

    ProductCategoryResponse productCategoryToProductCategoryResponseDTO(ProductCategoryEntity request);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "products", source = "products")
    List<ProductCategoryResponse > toProductCateroiesResponseDTO(List<ProductCategoryEntity> request);

    @Mapping(target = "products", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductCategory(@MappingTarget ProductCategoryEntity productCategoryEntity, UpdateProductCategoryRequest request);
}
