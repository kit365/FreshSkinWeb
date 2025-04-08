package com.kit.maximus.freshskinweb.service.payment;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    String createPayment(String id, HttpServletRequest ipAddr);
    String handleIPN(Map<String, String> params);

}
