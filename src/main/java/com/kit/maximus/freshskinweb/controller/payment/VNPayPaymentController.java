package com.kit.maximus.freshskinweb.controller.payment;


import com.kit.maximus.freshskinweb.dto.response.CreateMomoResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.payment.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/vnpay")
public class VNPayPaymentController {

    VnPayService vnPayService;

//    @GetMapping("/create")
//    public ResponseAPI<String> createPayment(@RequestParam String orderId) {
//        String paymentUrl = vnPayService.createPayment(orderId);
//        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(paymentUrl).build();
//    }

//    @GetMapping("/create")
//    public ResponseAPI<byte[]> createPayment(@RequestParam String orderId) {
//        byte[] qrCodeImage = vnPayService.createPaymentQRCode(orderId);
//        return ResponseAPI.<byte[]>builder()
//                .code(HttpStatus.OK.value())
//                .data(qrCodeImage)
//                .build();
//    }

    @GetMapping("/create")
    public ResponseAPI<String> createPayment(@RequestParam String orderId, HttpServletRequest request) {
//        String clientIp = request.getRemoteAddr(); // Lấy IP của người dùng
//        System.out.println("Client IP: " + clientIp); // Debug IP
        System.out.println(request);
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
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("https://project-swp391-n9j6.onrender.com/order/success/"+id))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán thất bại!");
        }
    }
}




