package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.discount.CreationDiscountRequest;
import com.kit.maximus.freshskinweb.dto.request.discount.UpdationtionDiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(target = "promoCode", ignore = true)
    @Mapping(target = "userDiscountUsageEntities", ignore = true)
    DiscountEntity toDiscountEntity(CreationDiscountRequest request);

    DiscountResponse toDiscountResponse(DiscountEntity entity);

    List<DiscountResponse> toDiscountsResponse(List<DiscountEntity> entity);

    @Mapping(target = "used", ignore = true)
    @Mapping(target = "promoCode", ignore = true)
    @Mapping(target = "userDiscountUsageEntities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateDiscountEntity(@MappingTarget DiscountEntity entity, UpdationtionDiscountRequest request);
}
