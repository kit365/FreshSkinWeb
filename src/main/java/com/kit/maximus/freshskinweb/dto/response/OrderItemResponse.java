package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse implements Serializable {
    Long orderItemId;

    OrderEntity order;

    ProductVariantEntity product;


    Integer quantity;

    Double subtotal;

    Status status;

    Boolean deleted;
}
