package com.kit.maximus.freshskinweb.dto.request.voucher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {
    @NotBlank(message = "Mã voucher không được để trống")
    String name;

    @NotNull(message = "Loại giảm giá không được để trống")
    DiscountType type;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm giá phải lớn hơn 0")
    BigDecimal discountValue;

    BigDecimal maxDiscount; // Mức giảm tối đa khi dùng PERCENTAGE

    BigDecimal minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng

    @NotNull(message = "Số lần sử dụng không được để trống")
    @Min(value = 1, message = "Số lần sử dụng phải lớn hơn 0")
    Integer usageLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    Date endDate;


}
