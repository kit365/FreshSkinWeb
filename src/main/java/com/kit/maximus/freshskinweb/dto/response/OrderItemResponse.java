package com.kit.maximus.freshskinweb.dto.response;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;

public class OrderItemResponse {
    Long orderItemId;

    OrderEntity order;

    ProductVariantEntity product;


    Integer quantity;

    Double subtotal;

    Status status;

    Boolean deleted;
}
