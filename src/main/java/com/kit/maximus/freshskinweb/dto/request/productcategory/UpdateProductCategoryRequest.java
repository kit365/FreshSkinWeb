package com.kit.maximus.freshskinweb.dto.request.productcategory;

import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class UpdateProductCategoryRequest implements Serializable {

    String title;

    String image;

    String description;

    Integer position;

    String status;

    boolean featured;
}
