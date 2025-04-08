package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = {"user", "voucher", "order"})
@Table(name = "OrderVouchers")
public class OrderVoucherEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderVoucherId")
    Long orderVoucherId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;  // Người đã lấy voucher

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    VoucherEntity voucher; // Voucher được sử dụng

    @ManyToOne
    @JoinColumn(name = "order_id")
    OrderEntity order; // Đơn hàng áp dụng voucher (nullable nếu user chỉ lấy mà chưa dùng)

    @Column(name = "used", nullable = false)
    Boolean used = false; // Đánh dấu voucher đã sử dụng hay chưa

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderVoucherEntity that = (OrderVoucherEntity) o;
        return getOrderVoucherId() != null && Objects.equals(getOrderVoucherId(), that.getOrderVoucherId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}


