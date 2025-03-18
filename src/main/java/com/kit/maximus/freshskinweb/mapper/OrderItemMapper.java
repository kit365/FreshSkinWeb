package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderItemResponse;
import com.kit.maximus.freshskinweb.entity.OrderItemEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    OrderItemEntity toOrderItemEntity(OrderItemRequest request);

//    @Mapping(target = "discountPrice", source = "discountPrice")
    OrderItemResponse toOrderItemResponse(OrderItemEntity orderItemEntity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public void updateOrderItems(@MappingTarget OrderItemEntity orderItemEntity, OrderItemRequest request);
}
