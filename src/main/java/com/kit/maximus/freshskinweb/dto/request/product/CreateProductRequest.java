package com.kit.maximus.freshskinweb.dto.request.product;

import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class CreateProductRequest implements Serializable {

    List<Long> categoryId;
    long brandId;
    String title;
    String description;
    List<MultipartFile> thumbnail;

    List<ProductVariantEntity> variants;

    List<Long> skinTypes;
    int discountPercent;
    Integer position;
    String origin;
    String ingredients;
    String usageInstructions;
    String benefits;
    String skinIssues;
    boolean featured;
    String status;

}
