package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Vouchers")
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "VoucherId")
    String voucherId;

    @Column(name = "Name",nullable = false)
    String name; // Mã voucher

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false)
    DiscountType type; // PERCENTAGE hoặc FIXED

    @Column(name = "DiscountValue", nullable = false)
    BigDecimal discountValue; // % hoặc số tiền giảm

    @Column(name = "MaxDiscount")
    BigDecimal maxDiscount; // Mức giảm tối đa khi dùng PERCENTAGE

    @Column(name = "MinOrderValue")
    BigDecimal minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng

    @Column(name = "UsageLimit", nullable = false)
    Integer usageLimit; // Số lần tối đa có thể sử dụng

    @Column(name = "Used", nullable = false)
    Integer used = 0; // Số lần đã sử dụng

    @Temporal(TemporalType.DATE)
    @Column(name = "StartDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "EndDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    Date endDate;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderVoucherEntity> orderVouchers = new ArrayList<>();

    public boolean isValid() {
        Date now = new Date();
        return used < usageLimit && now.after(startDate) && now.before(endDate);
    }


}
