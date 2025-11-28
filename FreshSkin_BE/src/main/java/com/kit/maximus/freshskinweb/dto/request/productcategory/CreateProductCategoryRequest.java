package com.kit.maximus.freshskinweb.dto.request.productcategory;

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
public class CreateProductCategoryRequest implements Serializable {
    String title;

    String description;

    Integer position;

    List<MultipartFile> image;

    List<ChildCategoryDTO> child;

    Long parentID;

    String status ;

    Boolean featured;


}
