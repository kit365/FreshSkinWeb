package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class ProductResponseDTO implements Serializable {
    Long id;
    ProductCategoryResponse category;
    //    String DiscountID;
    String title;
    String slug;
    String description;
    String thumbnail;
    List<ProductVariantEntity> variants;
    int discountPercent;
    Integer position;
    String brand;
    String skinType;
    /// ////////////////////////////////////////////
    String origin;
    String ingredients;
    String usageInstructions;
    String benefits;
    String skinIssues;
//    String thumbnailPayload;
    /// ////////////////////////////////////////////
    boolean featured;
    String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

}
