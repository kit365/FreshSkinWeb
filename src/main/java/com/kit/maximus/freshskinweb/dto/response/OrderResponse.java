package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse implements Serializable {

    String orderId;

    Long userId;

    String username;

    String typeUser;

    String firstName;

    String lastName;

    String email;

    String address;

    String phoneNumber;

    Long totalAmount;

    BigDecimal totalPrice;

    PaymentMethod paymentMethod;

    List<OrderItemResponse> orderItems;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date orderDate;

    Status status;

    Boolean deleted;

    String orderStatus;

    String paymentStatus;

    VoucherResponse voucher; // Mã giảm giá nếu có

    BigDecimal discountAmount; // Số tiền giảm giá

    BigDecimal priceShipping;


}
