package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    ProductEntity productToProductEntity(CreateProductRequest productRequest);

    ProductResponseDTO productToProductResponseDTO(ProductEntity product);

//    List<ProductResponseDTO> toUserResponseDTO(List<ProductEntity> productEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(@MappingTarget ProductEntity user, UpdateProductRequest productRequestDTO);

}
