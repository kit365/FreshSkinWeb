package com.kit.maximus.freshskinweb.dto.request.discount;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdationtionDiscountRequest {

    String PromoCode;

    String Description;

    String DiscountType;

    Double DiscountValue;

    Double MaxDiscount;

    Integer UsageLimit;

    Integer Used;

}
