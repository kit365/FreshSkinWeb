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

    List<MultipartFile> newImg;

    List<String> thumbnail;

    String description;

    Long parentID;

//    List<ChildCategoryDTO> child;

    Boolean featured;


}
