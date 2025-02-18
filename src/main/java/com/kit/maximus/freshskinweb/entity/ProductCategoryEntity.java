package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "ProductCategory")
public class ProductCategoryEntity extends AbstractEntity {

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

    @Column(name = "image")
    String image;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "category")
    List<ProductEntity> products = new ArrayList<>();

    public void createProduct(ProductEntity product) {
        products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(ProductEntity product) {
        products.remove(product);
        product.setCategory(null);
    }



}