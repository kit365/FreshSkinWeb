package com.kit.maximus.freshskinweb.dto.request.product;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class UpdateProductRequest implements Serializable {

    String productDetailID;
    List<Long> categoryId;
    long brandId;
//    String DiscountID;
    String title;
    String description;
    List<MultipartFile> thumbnail;
    List<ProductVariantEntity> variants;
    List<SkinTypeEntity> skinTypes;
    int discountPercent;
    Integer position;
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
