package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.*;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse implements Serializable {
    Long orderId;

    @JsonIgnore
    UserEntity user;

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
