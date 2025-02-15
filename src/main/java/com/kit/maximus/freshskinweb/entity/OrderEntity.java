package com.kit.maximus.freshskinweb.entity;

import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Orders")
public class OrderEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderId")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    UserEntity user;

    @Column(name = "Username")
    String username;

    @Column(name = "FirstName")
    String firstName;

    @Column(name = "LastName")
    String lastName;

    @Column(name = "Email")
    String email;

    @Column(name = "Address")
    String address;

    @Column(name = "PhoneNumber")
    String phoneNumber;

    @Column(name = "TotalAmount")
    long totalAmount;

    @Column(name = "TotalPrice")
    double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentMethod")
    PaymentMethod paymentMethod;

    @Column(name = "OrderDate")
    LocalDate OrderDate;



}
