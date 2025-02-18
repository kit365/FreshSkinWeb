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
public class UpdateProductBrandRequest implements Serializable {

    String title;

    String image;

    String description;

    Integer position;

    String status;

    boolean featured;
}
