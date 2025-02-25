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
public class UpdateProductCategoryRequest implements Serializable {

    String title;

    List<MultipartFile> image;

    String description;

    Integer position;

    String status;

    Long parentID;

    List<ChildCategoryDTO> child;

    boolean featured;
}
