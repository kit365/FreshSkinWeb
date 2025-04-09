package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@With
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {
    String voucherId;
    String name;
    DiscountType type;
    BigDecimal discountValue;
    BigDecimal maxDiscount;
    BigDecimal minOrderValue;
    Integer usageLimit;
    Integer used;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date endDate;

    String status;

    Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date updatedAt;

}
