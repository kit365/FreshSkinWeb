package com.kit.maximus.freshskinweb.dto.request.order;

import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderRequest implements Serializable {

    Long userId;

    @NotBlank(message = "FIRSTNAME_NOT_BLANK")
    @Size(min = 2, max = 50, message = "FIRSTNAME_INVALID")
    String firstName;

    @NotBlank(message = "LASTNAME_NOT_BLANK")
    @Size(min = 2, max = 50, message = "LASTNAME_INVALID")
    String lastName;

    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "ADDRESS_NOT_BLANK")
    @Size(min = 5, max = 255, message = "ADDRESS_INVALID")
    String address;

    @NotBlank(message = "PHONE_NUMBER_NOT_BLANK")
    @Pattern(regexp = "\\d{10}", message = "PHONE_NUMBER_INVALID")
    String phoneNumber;

    @NotNull(message = "TOTAL_AMOUNT_NOT_NULL")
    @Min(value = 1, message = "TOTAL_AMOUNT_INVALID")
    Long totalAmount;

    @NotNull(message = "TOTAL_PRICE_NOT_NULL")
    @DecimalMin(value = "0.0", inclusive = false, message = "TOTAL_PRICE_INVALID")
    Double totalPrice;

    @NotNull(message = "PAYMENT_METHOD_NOT_NULL")
    PaymentMethod paymentMethod;

    @NotNull(message = "ORDER_DATE_NOT_NUL")
    LocalDate orderDate;

}
