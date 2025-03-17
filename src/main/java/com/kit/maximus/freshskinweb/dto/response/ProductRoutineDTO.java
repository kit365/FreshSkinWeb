package com.kit.maximus.freshskinweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRoutineDTO {
    private Long id;
    private String title;
    private String slug;
    private List<String> thumbnail;
    private List<ProductVariantResponse> variants;
    private ProductBrandResponse brand;
    private List<ProductCategoryResponse> category;

}