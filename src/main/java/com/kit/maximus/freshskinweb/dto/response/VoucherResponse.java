package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    Double discountValue;
    Double maxDiscount;
    Double minOrderValue;
    Integer usageLimit;
    Integer used;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date endDate;
}
