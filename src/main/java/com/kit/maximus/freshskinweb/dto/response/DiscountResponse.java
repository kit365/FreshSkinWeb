package com.kit.maximus.freshskinweb.dto.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DiscountResponse {

    Long id;

    String PromoCode;

    String Description;

    String DiscountType;

    Double DiscountValue;

    Double MaxDiscount;

    Integer UsageLimit;

    Integer Used;
}
