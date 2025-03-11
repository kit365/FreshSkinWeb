package com.kit.maximus.freshskinweb.controller;


import com.kit.maximus.freshskinweb.service.EmailService;
import com.kit.maximus.freshskinweb.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
public class MailController {
    EmailService emailService;
    OrderService orderService;

    @PostMapping("/send-mail")
    public ResponseEntity<String> sendTestMail(@RequestParam String orderId) {
        orderService.processOrder(orderId);
        return ResponseEntity.ok("Gửi email xác nhận đơn hàng #" + orderId + " thành công!");
    }
}
