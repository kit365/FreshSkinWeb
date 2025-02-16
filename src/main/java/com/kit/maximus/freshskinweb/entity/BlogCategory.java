package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "BlogCategory")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogCategory extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlogCategoryID", insertable = false, updatable = false)
    Long blogCategoryId;

    @Column(name = "Categoryname")
    String blogCategoryName;

    @Column(name = "Description")
    String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "blogCategory",orphanRemoval = true)
    List<BlogEntity> blog;


//    public void createProductVariant(ProductVariantEntity productVariantEntity) {
//        variants.add(productVariantEntity);
//        productVariantEntity.setProduct(this);
//    }
//
//
//    public void removeProductVariant(ProductVariantEntity productVariantEntity) {
//        variants.remove(productVariantEntity);
//        productVariantEntity.setProduct(null);
//    }
}
