package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.discount.CreationDiscountRequest;
import com.kit.maximus.freshskinweb.dto.request.discount.UpdationtionDiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountEntity toDiscountEntity(CreationDiscountRequest request);

    DiscountResponse toDiscountResponse(DiscountEntity entity);

    List<DiscountResponse> toDiscountsResponse(List<DiscountEntity> entity);

    @Mapping(target = "used", ignore = true)
    void updateDiscountEntity(@MappingTarget DiscountEntity entity, UpdationtionDiscountRequest request);
}
