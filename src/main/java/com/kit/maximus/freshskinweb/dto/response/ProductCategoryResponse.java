package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class ProductCategoryResponse implements Serializable {

    Long id;

    String title;

    String slug;

    String description;

    int position;

    String image;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductEntity> product;


    boolean featured;

    String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

}
