package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "ProductVariant")
public class ProductVariantEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductVariantId")
    Long id;

    @Column(name = "Price")
    double price;

    @Column(name = "Volume")
    double volume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID")
    ProductEntity product;

}
