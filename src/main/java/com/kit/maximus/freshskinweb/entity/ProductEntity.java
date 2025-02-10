package com.kit.maximus.freshskinweb.entity;


import com.kit.maximus.freshskinweb.utils.SkinType;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "ProductDetailID")
    String productDetailID;

    @Column(name = "CategoryID")
    String categoryID;

    @Column(name = "DiscountID")
    String DiscountID;

    @Column(name = "Title")
    String title;

    @Column(name = "Slug")
    String slug;

    @Column(columnDefinition = "Text", name = "Description")
    String description;

    @Column(name = "Thumbnail")
    String thumbnail;

    @Column(name = "Stock")
    int stock;

    @Column(columnDefinition = "TEXT")
    String priceByVolume;

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

//    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE SET NULL,
//    FOREIGN KEY (DiscountID) REFERENCES Discount(DiscountID) ON DELETE SET NULL,
//    FOREIGN KEY (ProductDetailID) REFERENCES ProductDetails(ProductDetailID) ON DELETE SET NULL
//);


}
