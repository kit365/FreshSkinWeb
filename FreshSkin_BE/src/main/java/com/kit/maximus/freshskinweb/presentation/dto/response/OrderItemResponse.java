package com.kit.maximus.freshskinweb.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.dataaccess.entity.order.OrderEntity;
import com.kit.maximus.freshskinweb.common.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemResponse implements Serializable {
    Long orderItemId;

    @JsonIgnore //tạm thời ẩn bớt
    OrderEntity order;

    ProductVariantResponse productVariant;

    Integer quantity;

    BigDecimal subtotal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Status status;

}
