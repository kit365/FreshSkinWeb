package com.kit.maximus.freshskinweb.dto.request.productcategory;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class CreateProductCategoryRequest implements Serializable {

    String title;

    String image;

    String slug;

    String description;

    int position;

    boolean featured;
}
