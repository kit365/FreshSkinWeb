package com.kit.maximus.freshskinweb.dto.request.product;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class UpdateProductRequest implements Serializable {

    String productDetailID;
    long categoryId;
    long brandId;
//    String DiscountID;
    String title;
    String description;
    String thumbnail;
    List<ProductVariantEntity> variants;
    int discountPercent;
    Integer position;
    String skinType;
    /// ////////////////////////////////////////////
    String origin;
    String ingredients;
    String usageInstructions;
    String benefits;
    String skinIssues;
    /// ////////////////////////////////////////////
    boolean featured;
    String status;

}
