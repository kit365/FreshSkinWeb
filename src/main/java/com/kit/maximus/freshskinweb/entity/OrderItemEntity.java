package com.kit.maximus.freshskinweb.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "OrderItem")
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
    Double subtotal;

    @Column(name = "DiscountPrice")
    Double discountPrice; // Tổng tiền sau giảm giá

//    public void calculateSubtotal() {
//        if (productVariant == null || productVariant.getProduct() == null) {
//            this.subtotal = 0.0;
//            this.discountPrice = 0.0;
//            return;
//        }
//
//        Double pricePerUnit = (productVariant.getPrice() != 0) ? productVariant.getPrice() : 0.0;
//        Double discountPercent = (productVariant.getProduct().getDiscount() != null)
//                ? productVariant.getProduct().getDiscount().getDiscountPercentage() / 100.0
//                : 0.0;
//
//        this.subtotal = pricePerUnit * quantity; // Giá gốc
//        this.discountPrice = subtotal * (1 - discountPercent); // Giá sau giảm
//    }

}
