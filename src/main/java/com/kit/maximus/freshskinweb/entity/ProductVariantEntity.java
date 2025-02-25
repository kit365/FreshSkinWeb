package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orderItems", "product"})
@Table(name = "ProductVariant")
public class ProductVariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductVariantId")
    Long id;

    @Column(name = "Price")
    double price;

    @Column(name = "Volume")
    int volume;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID")
    @ToString.Exclude
    ProductEntity product;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "productVariant")
    @ToString.Exclude
    List<OrderItemEntity> orderItems;




}
