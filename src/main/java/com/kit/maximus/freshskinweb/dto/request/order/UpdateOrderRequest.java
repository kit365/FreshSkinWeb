package com.kit.maximus.freshskinweb.dto.request.order;

import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;

import java.time.LocalDate;

public class UpdateOrderRequest {
//    UserEntity user;

    String username;

    String firstName;

    String lastName;

    String email;

    String address;

    String phoneNumber;

    Long totalAmount;

    Double totalPrice;

    PaymentMethod paymentMethod;

    LocalDate OrderDate;
}
