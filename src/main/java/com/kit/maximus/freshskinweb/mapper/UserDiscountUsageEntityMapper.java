package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.user_discount_usage.CreationUserDiscountUsageRequest;
import com.kit.maximus.freshskinweb.dto.request.user_discount_usage.UpdationUserDiscountUsageRequest;
import com.kit.maximus.freshskinweb.dto.response.UserDiscountUsageResponse;
import com.kit.maximus.freshskinweb.entity.UserDiscountUsageEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserDiscountUsageEntityMapper {
    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "discountEntity", ignore = true)
    UserDiscountUsageEntity toEntity(CreationUserDiscountUsageRequest request);

    UserDiscountUsageResponse toResponse(UserDiscountUsageEntity entity);

    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "discountEntity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget UserDiscountUsageEntity entity, UpdationUserDiscountUsageRequest request);
}
