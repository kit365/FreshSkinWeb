package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class ProductResponseDTO implements Serializable {
    Long id;
    List<ProductCategoryEntity> category;
    ProductBrandResponse brand;
    //    String DiscountID;
    String title;
    String slug;
    String description;
//    List<String> thumbnail;
    List<String> thumbnail;
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
//    String thumbnailPayload;
    /// ////////////////////////////////////////////
    boolean featured;
    String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

}
