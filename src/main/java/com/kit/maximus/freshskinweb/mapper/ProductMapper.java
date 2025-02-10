package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.ProductRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity productToProductEntity(ProductRequestDTO product);
    ProductResponseDTO productToProductResponseDTO(ProductEntity product);

    @Mapping(target = "id", ignore = true) //không update id
    void updateProduct(@MappingTarget ProductEntity productEntity, ProductRequestDTO productRequestDTO);
}
