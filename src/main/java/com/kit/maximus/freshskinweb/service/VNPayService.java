package com.kit.maximus.freshskinweb.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service
public class VNPayService {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    public String createPayment(Long amount, String orderId) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String vnp_IpAddr = "127.0.0.1"; // Lấy IP thực tế từ request nếu cần

        // Định dạng thời gian theo yêu cầu của VNPay
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(calendar.getTime());

        // Thời gian hết hạn sau 15 phút
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());

        // Tạo danh sách tham số gửi lên VNPay
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", vnp_Version);
        params.put("vnp_Command", vnp_Command);
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay tính theo VND * 100
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderId);
        params.put("vnp_OrderInfo", "Thanh toan don hang: " + orderId);
        params.put("vnp_OrderType", orderType);
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_IpAddr", vnp_IpAddr);
        params.put("vnp_CreateDate", vnp_CreateDate);
        params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp tham số theo thứ tự ABC để tạo chữ ký
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append("&");
                query.append(fieldName).append("=").append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append("&");
            }
        }

        // Xóa ký tự `&` cuối cùng
        hashData.deleteCharAt(hashData.length() - 1);
        query.deleteCharAt(query.length() - 1);

        // Tạo chữ ký bảo mật
        String vnp_SecureHash = HmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnp_PayUrl + "?" + query;
    }

    // Hàm mã hóa HMAC SHA512
    public String HmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa HMAC SHA512", e);
        }
    }
}

