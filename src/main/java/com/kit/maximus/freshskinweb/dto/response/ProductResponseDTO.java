package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDTO implements Serializable {

    Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductCategoryResponse> category;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ProductBrandResponse brand;
    //    String DiscountID;
    String title;
    String slug;
    String description;
    List<String> thumbnail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductVariantResponse> variants;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SkinTypeEntity> skinTypes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    int discountPercent;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer position;
    /// ////////////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String origin;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String ingredients;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String usageInstructions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String benefits;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String skinIssues;
    /// ////////////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_NULL)
    boolean featured;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

}
