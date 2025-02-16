package com.kit.maximus.freshskinweb.dto.request.productvariant;

import com.kit.maximus.freshskinweb.entity.ProductEntity;
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
public class UpdateProductVariant implements Serializable {

    double price;
    double volume;
    ProductEntity product;

}
