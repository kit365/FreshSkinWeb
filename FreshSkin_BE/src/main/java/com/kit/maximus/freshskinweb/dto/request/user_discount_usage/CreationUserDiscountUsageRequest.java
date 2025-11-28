package com.kit.maximus.freshskinweb.dto.request.user_discount_usage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreationUserDiscountUsageRequest {
    Long userID;
    String promoCodeID;
    Boolean discountStatus;

}
