package com.kit.maximus.freshskinweb.dto.request;

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
public class ProductRequestDTO implements Serializable {
    Long id;
    String productDetailID;
    String categoryID;
    String DiscountID;
    String title;
    String slug;
    String description;
    String thumbnail;
    int stock;
    String priceByVolume;
    int discountPercent;
    int position;
    String brand;
    String skinType;
    boolean featured;
    String status;
}
