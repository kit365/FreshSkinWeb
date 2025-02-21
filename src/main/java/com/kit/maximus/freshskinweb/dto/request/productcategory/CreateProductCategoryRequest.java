package com.kit.maximus.freshskinweb.dto.request.productcategory;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
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
public class CreateProductCategoryRequest implements Serializable {

    String title;

    String image;


    String description;

    Integer position;

    boolean featured;

    List<ChildCategoryDTO> child;

    long parentID;

}

