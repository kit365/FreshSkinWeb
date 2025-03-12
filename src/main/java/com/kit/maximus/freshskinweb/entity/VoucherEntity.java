package com.kit.maximus.freshskinweb.entity;

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

    @Column(name = "Code",nullable = false, unique = true)
    String code; // Mã voucher

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StartDate", nullable = false)
    Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EndDate", nullable = false)
    Date endDate;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderEntity> orders = new ArrayList<>();

    public boolean isValid() {
        Date now = new Date();
        return used < usageLimit && now.after(startDate) && now.before(endDate);
    }

    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (type == DiscountType.PERCENTAGE) {
            BigDecimal discount = orderTotal.multiply(discountValue).divide(BigDecimal.valueOf(100));
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                return maxDiscount;
            }
            return discount;
        }
        return discountValue; // Nếu là FIXED thì trả về số tiền giảm cố định
    }

    public void applyVoucher(OrderEntity orderEntity) {
        orders.add(orderEntity);
        orderEntity.setVoucher(this);
    }

    public void removeVoucher(OrderEntity orderEntity) {
        orders.remove(orderEntity);
        orderEntity.setVoucher(null);
    }


}
