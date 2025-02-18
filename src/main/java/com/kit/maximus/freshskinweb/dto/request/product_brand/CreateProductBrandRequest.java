package com.kit.maximus.freshskinweb.dto.request.product_brand;

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
public class CreateProductBrandRequest implements Serializable {

    String title;

    String image;

    String slug;

    String description;

    Integer position;

    boolean featured;
}
