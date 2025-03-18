package com.kit.maximus.freshskinweb.dto.request.order;

import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderRequest extends AbstractEntity implements Serializable {

    Long userId;

    String firstName;

    String lastName;

    String email;

    String address;

    String phoneNumber;

    Long totalAmount;

    String paymentMethod;

    String orderStatus;

    String paymentStatus;

    List<OrderItemRequest> orderItems;


    String voucherId;
}
