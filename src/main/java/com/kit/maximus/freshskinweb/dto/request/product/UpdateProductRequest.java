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
public class UpdateProductRequest implements Serializable {
    List<Long> categoryId;
    long brandId;
//    String DiscountID;
    String title;
    String description;
    List<MultipartFile> newImg;
    List<String> image;
    List<ProductVariantEntity> variants;
    List<Long> skinTypeId;
//    DiscountEntity discountEntity;
    int discountPercent;
    String origin;
    String ingredients;
    String usageInstructions;
    String benefits;
    String skinIssues;
    boolean featured;
    int stock;

}
