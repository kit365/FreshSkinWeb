package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
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

    Double subtotal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Status status;

    Double discountPrice;
}
