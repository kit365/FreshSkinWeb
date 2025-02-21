package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class ProductCategoryResponse implements Serializable {

    Long id;

    String title;

    String slug;

    String description;

    Integer position;

    String image;

    boolean featured;

    String status;

    boolean deleted;

    List<ProductCategoryResponse> child;

    List<ProductEntity> products;

    // Chỉ áp dụng với danh mục cha
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String updatedAt;
}
