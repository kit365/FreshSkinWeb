package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String description;
    List<String> thumbnail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductVariantResponse> variants;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SkinTypeResponse> skinTypes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnore
    DiscountResponse discountEntity;
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
    Boolean featured;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

    boolean deleted;


}
