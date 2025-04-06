package com.kit.maximus.freshskinweb.entity;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@With
@Table(name = "Product", indexes = {
        @Index(name = "idx_title", columnList = "Title"),
        @Index(name = "idx_slug", columnList = "Slug"),
        @Index(name = "idx_brand", columnList = "brandID"),
})
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "category", "variants", "skinTypes", "reviews"})
public class ProductEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductId")
    Long id;

    //quan he
//    @Column(name = "DiscountID")
//    DiscoundEntity discount;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "Product_Category",
            joinColumns = @JoinColumn(name = "productID"),
            inverseJoinColumns = @JoinColumn(name = "categoryID")
    )
    List<ProductCategoryEntity> category = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brandID")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    ProductBrandEntity brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("price ASC")
    List<ProductVariantEntity> variants = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ProductSkinType",
            joinColumns = @JoinColumn(name = "product_id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "skin_type_id", nullable = true)
    )
    List<SkinTypeEntity> skinTypes = new ArrayList<>();

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(columnDefinition = "MEDIUMTEXT", name = "Description")
    String description;

//    @ElementCollection // Lưu danh sách ảnh trong một bảng riêng
//     List<String> thumbnail;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "Thumbnail")
    List<String> thumbnail;

    @Column(name = "DiscountPercentage")
    Double discountPercent;

    @Column(name = "Position")
    Integer position;

    @Column(name = "Featured")
    Boolean featured;

    /// /////////////
    @Column(name = "Origin", columnDefinition = "MEDIUMTEXT")
    String origin;

    @Column(name = "Ingredients", columnDefinition = "MEDIUMTEXT")
    String ingredients;

    @Column(name = "UsageInstructions", columnDefinition = "MEDIUMTEXT")
    String usageInstructions;

    @Column(name = "Benefits", columnDefinition = "MEDIUMTEXT")
    String benefits;

    @Column(name = "SkinIssues", columnDefinition = "MEDIUMTEXT")
    String skinIssues;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "product")
    List<ReviewEntity> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RountineStepId")
    RountineStepEntity rountineStep;

//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "discountId")
//    DiscountEntity discount;

    public void createProductVariant(ProductVariantEntity productVariantEntity) {
        variants.add(productVariantEntity);
        productVariantEntity.setProduct(this);
    }

    public void removeProductVariant(ProductVariantEntity productVariantEntity) {
        variants.remove(productVariantEntity);
        productVariantEntity.setProduct(null);
    }

}
