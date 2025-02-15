package com.kit.maximus.freshskinweb.entity;


import com.kit.maximus.freshskinweb.utils.SkinType;
import jakarta.persistence.*;
import lombok.*;

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

//    @Column(name = "CategoryID")
//    String categoryID;

    //    Mapper: Ánh xạ với fields bên N(product)
//    @JoinColumn(name = "ProductID") không cần vì bên nhiều giữ khóa ngoại của bên 1 nên không cần
    //Xóa một đối tượng bên N không còn map với bên 1 nữa -> orphanRemoval = true
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    List<ProductVariantEntity> variants;

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
    int position;

    @Column(name = "Brand")
    String brand;

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


    /// ///////


    public void createProductVariant(ProductVariantEntity productVariantEntity) {
        variants.add(productVariantEntity);
        productVariantEntity.setProduct(this);
    }

    public void removeProductVariant(ProductVariantEntity productVariantEntity) {
        variants.remove(productVariantEntity);
    }

//    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE SET NULL,
//    FOREIGN KEY (DiscountID) REFERENCES Discount(DiscountID) ON DELETE SET NULL,
//    FOREIGN KEY (ProductDetailID) REFERENCES ProductDetails(ProductDetailID) ON DELETE SET NULL
//);


}
