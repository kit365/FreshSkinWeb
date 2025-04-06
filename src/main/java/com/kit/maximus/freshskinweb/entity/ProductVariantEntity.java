package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.*;
import com.kit.maximus.freshskinweb.utils.UnitType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@With
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orderItems", "product"})
@Table(name = "ProductVariant", indexes = {
        @Index(name = "idx_product", columnList = "ProductID")
})

public class ProductVariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductVariantId")
    Long id;

    @Column(name = "Price")
    BigDecimal price;

    @Column(name = "Volume")
    int volume;

    @Column(name = "Stock")
    int stock;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID")
    @ToString.Exclude
    ProductEntity product;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    UnitType unit;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "productVariant")
    @ToString.Exclude
    List<OrderItemEntity> orderItems;




}
