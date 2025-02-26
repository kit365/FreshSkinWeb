package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderEntity toOrderEntity(OrderRequest order);

    @Mapping(target = "orderItems", source = "orderItems")
    OrderResponse toOrderResponse(OrderEntity order);

    List<OrderResponse> toOrderResponseList(List<OrderEntity> orderEntities);


//    Optional<OrderResponse> toOrderEntityOptional(OrderResponse order);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateOrder(@MappingTarget OrderEntity order, UpdateOrderRequest update);

//    OrderEntity toOrderEntity(UserEntity user);

    @Mapping(target = "orderId", ignore = true) // Bỏ qua ID vì nó tự tăng
    @Mapping(target = "user", source = "user", ignore = false) // Map UserEntity vào OrderEntity
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phone")
    @Mapping(target = "address", source = "user.address")
    OrderEntity toOrderEntity(OrderRequest request, UserEntity user);
}
