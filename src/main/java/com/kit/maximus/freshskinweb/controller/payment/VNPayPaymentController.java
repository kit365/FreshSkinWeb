package com.kit.maximus.freshskinweb.controller.payment;


import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.order.OrderService;
import com.kit.maximus.freshskinweb.service.payment.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/vnpay")
public class VNPayPaymentController {

    VnPayService vnPayService;

    OrderService orderService;

    @GetMapping("/create")
    public ResponseAPI<String> createPayment(@RequestParam String orderId, HttpServletRequest request) {
        String paymentUrl = vnPayService.createPayment(orderId, request);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(paymentUrl).build();
    }

    @GetMapping("/payment-return")
    public ResponseEntity<String> vnpayReturn(
            @RequestParam Map<String, String> queryParams) {
        String id = vnPayService.handleIPN(queryParams);
        // Kiểm tra mã giao dịch hợp lệ
        String transactionStatus = queryParams.get("vnp_TransactionStatus");
        if ("00".equals(transactionStatus)) {
            orderService.processOrder(id);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/order/success/"+id))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/order/fail"))
                    .build();
        }
    }

}




