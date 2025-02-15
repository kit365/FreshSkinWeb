package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity productToProductEntity(CreateProductRequest productRequest);


    ProductResponseDTO productToProductResponseDTO(ProductEntity product);

    List<ProductResponseDTO> toUserResponseDTO(List<ProductEntity> productEntities);

    @Mapping(target = "id", ignore = true) //không update id
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) //neu request null thi ko thay doi cai cũ
    void updateProduct(@MappingTarget ProductEntity user,  UpdateProductRequest productRequestDTO);
}
