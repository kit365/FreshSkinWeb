package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import com.kit.maximus.freshskinweb.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "`order`", indexes = {
        @Index(name = "idx_order_status", columnList = "OrderStatus"),
        @Index(name = "idx_order_id", columnList = "OrderId"),
        @Index(name = "idx_first_last_name", columnList = "FirstName,LastName"),
        @Index(name = "idx_updated_at", columnList = "Update_at")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEntity extends AbstractEntity {

    @Id
    @Column(name = "OrderId")
    String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = true)
    UserEntity user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "order")
    List<OrderItemEntity> orderItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<NotificationEntity> notifications = new ArrayList<>();


    @Column(name = "FirstName")
    @JsonIgnore
    String firstName;

    @Column(name = "LastName")
    @JsonIgnore
    String lastName;

    @Column(name = "Email")
    @JsonIgnore
    String email;

    @Column(name = "Address")
    @JsonIgnore
    String address;

    @Column(name = "PhoneNumber")
    @JsonIgnore
    String phoneNumber;

    @Column(name = "TotalAmount")
    Integer totalAmount;

    @Column(name = "TotalPrice")
    BigDecimal totalPrice;

    @Column(name = "DiscountAmount")
    BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentMethod")
    PaymentMethod paymentMethod;

    @Column(name = "OrderDate")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "OrderStatus") //Thông báo trạng thái cho đơn hàng
    OrderStatus orderStatus = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentStatus")
    PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderVoucherEntity> orderVouchers = new ArrayList<>();

    @Column(name = "PriceShipping")
    BigDecimal priceShipping;

    @ManyToOne
    @JoinColumn(name = "VoucherId", referencedColumnName = "VoucherId")
    VoucherEntity voucher;


    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
}
