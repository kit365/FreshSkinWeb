package com.kit.maximus.freshskinweb.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kit.maximus.freshskinweb.utils.SkinType;
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
@ToString
@Table(name = "Product")
public class ProductEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductId")
    Long id;

    //quan he
//    @Column(name = "DiscountID")
//    DiscoundEntity discount;


    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryID")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    ProductCategoryEntity category;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "BrandID")
//    ProductBrandEntity brand;

    //    Mapper: Ánh xạ với fields bên N(product)
//    @JoinColumn(name = "ProductID") không cần vì bên nhiều giữ khóa ngoại của bên 1 nên không cần
    //Xóa một đối tượng bên N không còn map với bên 1 nữa -> orphanRemoval = true
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<ProductVariantEntity> variants = new ArrayList<>();

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(columnDefinition = "Text", name = "Description")
    String description;

    @Column(name = "Thumbnail")
    String thumbnail;


    @Column(name = "DiscountPercentage")
    int discountPercent;

    @Column(name = "Position")
    Integer position;

    @Enumerated(EnumType.STRING)
    @Column(name = "SkinType")
    SkinType skinType = SkinType.NORMAL;

    @Column(name = "Featured")
    boolean featured;

    /// /////////////
    @Column(name = "Origin")
    String origin;

    @Column(name = "Ingredients")
    String ingredients;

    @Column(name = "UsageInstructions")
    String usageInstructions;

    @Column(name = "Benefits")
    String benefits;

    @Column(name = "SkinIssues")
    String skinIssues;

    public void createProductVariant(ProductVariantEntity productVariantEntity) {
        variants.add(productVariantEntity);
        productVariantEntity.setProduct(this);
    }


    public void removeProductVariant(ProductVariantEntity productVariantEntity) {
        variants.remove(productVariantEntity);
        productVariantEntity.setProduct(null);
    }

}
