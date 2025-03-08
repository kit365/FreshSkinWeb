package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.service.VNPayService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @GetMapping("/vnpay")
    public String createPayment(@RequestParam Long amount, @RequestParam String orderId) {
        String paymentUrl = vnPayService.createPayment(amount, orderId);
        return "<html><body><h3>Quét mã QR để thanh toán:</h3>"
                + "<img src='https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=" + paymentUrl + "' />"
                + "</body></html>";
    }

    @GetMapping("/vnpay-return")
    public String paymentReturn(@RequestParam Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");

        // Kiểm tra chữ ký bảo mật
        String hashData = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        String mySecureHash = vnPayService.HmacSHA512(vnPayService.getVnp_HashSecret(), hashData);

        if (mySecureHash.equals(vnp_SecureHash)) {
            return "Thanh toán thành công!";
        } else {
            return "Xác thực thất bại!";
        }
    }

}

