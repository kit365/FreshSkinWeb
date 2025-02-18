package com.kit.maximus.freshskinweb.dto.request.orderItem;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class UpdateOrderItemRequest implements Serializable {
    OrderEntity order;

    ProductVariantEntity product;


    Integer quantity;

    Double subtotal;
}
