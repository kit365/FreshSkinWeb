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
@Table(name = "ProductBrand", indexes = {
        @Index(name = "idx_product_brand_title", columnList = "title"),
        @Index(name = "idx_product_brand_slug", columnList = "slug")
})
public class ProductBrandEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(name = "image")
    @ElementCollection(fetch = FetchType.EAGER)
    List<String> image;

    @Column(name = "Description", columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "Position")
    int position;

    @Column(name = "Featured")
    boolean featured;


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "brand")
    private List<ProductEntity> products = new ArrayList<>();


}
