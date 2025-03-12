package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.discount.DiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(target = "discountId", ignore = true)
    @Mapping(target = "products", ignore = true)
    DiscountEntity toDiscountEntity(DiscountRequest request);

    DiscountResponse toDiscountResponse(DiscountEntity entity);

    List<DiscountResponse> toDiscountsResponse(List<DiscountEntity> entity);

    @Mapping(target = "used", ignore = true)
    @Mapping(target = "discountId", ignore = true)
    @Mapping(target = "products", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateDiscountEntity(@MappingTarget DiscountEntity entity, DiscountRequest request);
}
