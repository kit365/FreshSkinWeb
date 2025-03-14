package com.kit.maximus.freshskinweb.dto.response;

import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountResponse {
    String discountId;
    String name;
    BigDecimal discountPercentage;
    BigDecimal discountAmount;
    BigDecimal maxDiscount;
    Date startDate;
    Date endDate;
    Integer usageLimit;
    Integer used;
    Boolean isGlobal;
    DiscountType discountType;
}
