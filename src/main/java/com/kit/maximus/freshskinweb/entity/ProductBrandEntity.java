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
@Table(name = "ProductBrand")
public class ProductBrandEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(name = "image")
    String image;

    @Column(name = "Description", columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "Position")
    int position;

    @Column(name = "Featured")
    boolean featured;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "brand")
    private List<ProductEntity> products = new ArrayList<>();

//    public void addProduct(ProductEntity product) {
//        productEntities.add(product);
//        product.setBrand(this);
//    }
//
//    public void removeProduct(ProductEntity product) {
//        productEntities.remove(product);
//        product.setBrand(null);
//    }
}
