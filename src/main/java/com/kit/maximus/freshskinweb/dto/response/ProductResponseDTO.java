package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class ProductResponseDTO implements Serializable {

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

}
