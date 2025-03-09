package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`Order`")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "order"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    long totalAmount;

    @Column(name = "TotalPrice")
    double totalPrice;

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

    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
}
