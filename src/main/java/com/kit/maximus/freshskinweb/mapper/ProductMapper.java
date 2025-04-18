package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "skinTypes", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    ProductEntity productToProductEntity(CreateProductRequest productRequest);


    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "skinTypes", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "discountEntity", ignore = true)
    ProductResponseDTO productToProductResponseDTO(ProductEntity product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "skinTypes", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "discountEntity", ignore = true)
    List<ProductResponseDTO> productToProductResponsesDTO(List<ProductEntity> product);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "skinTypes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(@MappingTarget ProductEntity product, UpdateProductRequest productRequestDTO);

}
