package com.kit.maximus.freshskinweb.dto.request.discount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

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
}
