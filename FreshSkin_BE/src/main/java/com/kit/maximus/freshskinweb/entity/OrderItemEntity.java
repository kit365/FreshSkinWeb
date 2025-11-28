package com.kit.maximus.freshskinweb.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "OrderItem", indexes = {
        @Index(name = "idx_product_variant_id", columnList = "ProductVariantId"),
        @Index(name = "idx_order_id", columnList = "OrderId")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "order"})

public class OrderItemEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderItemId")
    Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderId", nullable = true)
    OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductVariantId", nullable = true)
    ProductVariantEntity productVariant;

    @Column(name = "Quantity")
    Integer quantity;

    @Column(name = "Subtotal")
    BigDecimal subtotal;

    @Column(name = "DiscountPrice")
    BigDecimal discountPrice; // Tổng tiền sau giảm giá


}
