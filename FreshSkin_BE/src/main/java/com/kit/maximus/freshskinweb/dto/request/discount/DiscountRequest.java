package com.kit.maximus.freshskinweb.dto.request.discount;

import com.kit.maximus.freshskinweb.utils.DiscountType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest implements Serializable {

    @Size(max = 255, message = "NAME_INVALID")
    String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "DISCOUNT_PERCENTAGE_INVALID_MIN")
    @DecimalMax(value = "100.0", message = "DISCOUNT_PERCENTAGE_INVALID_MAX")
    BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", inclusive = false, message = "DISCOUNT_AMOUNT_INVALID")
    BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "MAX_DISCOUNT_INVALID")
    BigDecimal maxDiscount;

    @FutureOrPresent(message = "START_DATE_INVALID")
    LocalDateTime startDate;

    @Future(message = "END_DATE_INVALID")
    LocalDateTime endDate;

    @Min(value = 1, message = "USAGE_LIMIT_INVALID")
    Integer usageLimit;

    Boolean isGlobal = false;

    DiscountType discountType;

    List<Long> productIds;

}
