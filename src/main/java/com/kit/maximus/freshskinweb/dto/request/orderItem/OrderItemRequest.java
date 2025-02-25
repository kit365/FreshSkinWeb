package com.kit.maximus.freshskinweb.dto.request.orderItem;

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
public class OrderItemRequest implements Serializable {
    Long orderId;
    Long productVariantId;
    Integer quantity;
    Double subtotal;
    String status;
}
