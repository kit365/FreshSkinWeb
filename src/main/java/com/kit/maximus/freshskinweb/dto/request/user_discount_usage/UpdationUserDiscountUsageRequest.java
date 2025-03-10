package com.kit.maximus.freshskinweb.dto.request.user_discount_usage;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdationUserDiscountUsageRequest {

    Long userID;
    String promoCodeID;
    Boolean discountStatus;
    Date UsedAt;
    Boolean DiscountStatus;

}
