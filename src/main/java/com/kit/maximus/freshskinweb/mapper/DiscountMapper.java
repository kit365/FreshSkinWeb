package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.discount.DiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

//    @Mapping(target = "promoCode", ignore = true)
//    @Mapping(target = "userDiscountUsageEntities", ignore = true)
//    @Mapping(target = "productEntities", ignore = true)
    DiscountEntity toDiscountEntity(DiscountRequest request);

    DiscountResponse toDiscountResponse(DiscountEntity entity);

    @Named("toDiscountResponseId")
    @Mapping(target = "discountId", source = "discountId")
    DiscountResponse toDiscountResponseId(DiscountEntity entity);

    List<DiscountResponse> toDiscountsResponse(List<DiscountEntity> entity);

    @Mapping(target = "used", ignore = true)
//    @Mapping(target = "promoCode", ignore = true)
//    @Mapping(target = "userDiscountUsageEntities", ignore = true)
//    @Mapping(target = "productEntities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateDiscountEntity(@MappingTarget DiscountEntity entity, DiscountRequest request);
}
