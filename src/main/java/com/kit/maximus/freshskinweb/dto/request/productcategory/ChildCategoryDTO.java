package com.kit.maximus.freshskinweb.dto.request.productcategory;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ChildCategoryDTO implements Serializable {
    String title;
    String slug;
    String description;
    Integer position;
    boolean featured;
    String image;
    String status;
}
