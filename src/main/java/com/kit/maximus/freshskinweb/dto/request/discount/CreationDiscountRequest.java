package com.kit.maximus.freshskinweb.dto.request.discount;

import com.kit.maximus.freshskinweb.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreationDiscountRequest {


    String PromoCode;

    String Description;

    String DiscountType;

    Double DiscountValue;

    Double MaxDiscount;

    Integer UsageLimit;

    Boolean isGlobal;

}
