package com.kit.maximus.freshskinweb.dto.request.product_brand;

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
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductBrandRequest implements Serializable {

    String title;

    List<MultipartFile> image;

    String description;

    Integer position;

    String status;

    boolean featured;
}
