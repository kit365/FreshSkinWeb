package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "ProductBrand")
public class ProductBrandEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(name = "Description", columnDefinition = "TEXT")
    String description;

    @Column(name = "Position")
    int position;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "categoryID")
    List<ProductEntity> productEntities = new ArrayList<>();

    public void addProduct(ProductEntity product) {
        productEntities.add(product);
       product.setBrand(this);
    }

    public void removeProduct(ProductEntity product) {
        productEntities.remove(product);
        product.setCategory(null);
    }
}
