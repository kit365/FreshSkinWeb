package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.OrderItemEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse implements Serializable {
    Long orderId;

    UserEntity user;

    List<OrderItemEntity> orderItems;

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

    Status Status;

    Boolean deleted;
}
