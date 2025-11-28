package com.kit.maximus.freshskinweb.dto.request.product_brand;

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
public class UpdateProductBrandRequest implements Serializable {
    String title;

    List<MultipartFile> newImg;

    List<String> image;

    String description;

    boolean featured;
}
