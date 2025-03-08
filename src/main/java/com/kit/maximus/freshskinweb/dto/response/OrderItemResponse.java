package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    Status status;
}
