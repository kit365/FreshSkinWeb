//package com.kit.maximus.freshskinweb.service;
//
//import com.kit.maximus.freshskinweb.constant.MomoApi;
//import com.kit.maximus.freshskinweb.dto.request.payment.CreateMomoRequest;
//import com.kit.maximus.freshskinweb.dto.response.CreateMomoResponse;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class MomoService {
//    @Value("${momo.partner-code}")
//    private String partnerCode;
//
//    @Value("${momo.access-key}")
//    private String accessKey;
//
//    @Value("${momo.secret-key}")
//    private String secretKey;
//
//    @Value("${momo.redirect-url}")
//    private String redirectUrl;
//
//    @Value("${momo.ipn-url}")
//    private String ipnUrl;
//
//    @Value("${momo.request-type}")
//    private String requestType;
//
//    private final MomoApi momoApi;
//
//    public CreateMomoResponse createQR() {
//        String orderId = UUID.randomUUID().toString();
//        String orderInfo = "Thanh toán đơn hàng" + orderId;
//        String requestId = UUID.randomUUID().toString();
//        String extraData = "Không có khuyến mãi";
//        Integer amount = 10000;
//        String rawSignature = String.format("accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s", accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId, requestType);
//
//        String prettySignature = "";
//        try {
//            prettySignature = new HmacSHA256Util().signHmacSHA256(rawSignature, secretKey);
//        } catch (Exception e) {
//            log.error(">>>Có lỗi khi hash code: {}", e);
//            return null;
//        }
//
//        if (prettySignature == null || prettySignature.isBlank()){
//            log.error(">>>Chữ ký rỗng");
//        }
//
//        CreateMomoRequest request = CreateMomoRequest.builder()
//                .partnerCode(partnerCode)
//                .requestType(requestType)
//                .ipnUrl(ipnUrl)
//                .redirectUrl(redirectUrl)
//                .orderId(orderId)
//                .orderInfo(orderInfo)
//                .requestId(requestId)
//                .extraData(extraData)
//                .signature(prettySignature)
//                .amount(amount)
//                .lang("vi")
//                .build();
//        return momoApi.create(request);
//    }
//
//
//
//    public class HmacSHA256Util {
//        public static String signHmacSHA256(String data, String key) throws Exception {
//            String algorithm = "HmacSHA256";
//            Mac hmacSHA256 = Mac.getInstance(algorithm);
//            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
//            hmacSHA256.init(secretKey);
//
//            byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append("0"); // Đảm bảo đủ 2 ký tự hex
//                }
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        }
//
//    }
//
//
//}
