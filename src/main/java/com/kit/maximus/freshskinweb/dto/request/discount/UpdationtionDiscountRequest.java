package com.kit.maximus.freshskinweb.dto.request.discount;

import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.*;

import java.util.List;

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

    Boolean isGlobal;

    Boolean active;

    boolean deleted;

}
