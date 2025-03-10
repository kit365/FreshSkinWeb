package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DiscountResponse{
    String promoCode;

    String description;

    String discountType;

    Double discountValue;

    Double maxDiscount;

    Integer usageLimit;

    Boolean isGlobal;

    Integer used;

    Boolean active;

    boolean deleted;

    List<UserDiscountUsageResponse> userDiscountUsageResponses;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;
}
