package com.kit.maximus.freshskinweb.service.payment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kit.maximus.freshskinweb.config.VnPayConfig;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.notification.NotificationEvent;
import com.kit.maximus.freshskinweb.service.notification.NotificationService;
import com.kit.maximus.freshskinweb.service.order.OrderService;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import com.kit.maximus.freshskinweb.utils.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VnPayService implements PaymentService {

    VnPayConfig vnPayConfig;

    OrderService orderService;

    ApplicationEventPublisher eventPublisher;


    private static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }


    @Override
    public String createPayment(String id, HttpServletRequest ipAddr) {
        try {
            OrderEntity order = orderService.getOrder(id);

            if (order.getOrderStatus().equals(OrderStatus.CANCELED) || order.getOrderStatus().equals(OrderStatus.COMPLETED)
            ) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            if (order.getPaymentMethod() == null) {
                throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
            }

            if (order.getPaymentStatus().equals(PaymentStatus.FAILED) || order.getPaymentStatus().equals(PaymentStatus.PAID)) {
                throw new AppException(ErrorCode.ORDER_INVALID);
            }

            String orderInfo = "Thanh toan don hang #" + order.getOrderId() + " - tong tien: " + order.getTotalAmount() + " VND";
            String version = "2.1.0";
            String command = "pay";
            String otherType = "130000";
            long amount = (long) (order.getTotalPrice().multiply(BigDecimal.valueOf(100)).doubleValue());
            ; // Format tiền theo cent
            String transactionReference = getRandomNumber(8);
            String clientIpAddress = getIpAddress(ipAddr);
//            String clientIpAddress = "127.0.0.1"; //localhost
            String teminal = vnPayConfig.getTmnCode();
            String return_url = vnPayConfig.getReturnUrl();


            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", version);
            params.put("vnp_Command", command);
            params.put("vnp_TmnCode", teminal);
            params.put("vnp_Amount", String.valueOf(amount));
            params.put("vnp_CurrCode", "VND");
//            params.put("vnp_TxnRef", transactionReference);
            params.put("vnp_TxnRef", order.getOrderId());
            params.put("vnp_OrderInfo", orderInfo);
            params.put("vnp_OrderType", otherType);
            params.put("vnp_Locale", "vn");
            params.put("vnp_ReturnUrl", return_url);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh"); // Đặt múi giờ Việt Nam

//            String vnp_CreateDate = LocalDateTime.now(zoneId).format(formatter); /neu bi loi khi render len sever
            String vnp_CreateDate = formatter.format(cld.getTime());

            params.put("vnp_CreateDate", vnp_CreateDate);
            params.put("vnp_IpAddr", clientIpAddress);

            // Sắp xếp params theo thứ tự a-z
            List<String> sortedKeys = new ArrayList<>(params.keySet());
            Collections.sort(sortedKeys);

            StringBuilder queryData = new StringBuilder();
            StringBuilder hashData = new StringBuilder();

            for (String key : sortedKeys) {
                String value = params.get(key);
                if (value != null) {
                    String encodedValue = URLEncoder.encode(value, StandardCharsets.US_ASCII);
                    if (!hashData.isEmpty()) {
                        hashData.append("&");
                    }
                    hashData.append(key).append("=").append(encodedValue);

                    if (!queryData.isEmpty()) {
                        queryData.append("&");
                    }
                    queryData.append(key).append("=").append(encodedValue);
                }
            }

            // Tạo vnp_SecureHash
            String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            queryData.append("&vnp_SecureHash=").append(secureHash);
            order.setPaymentStatus(PaymentStatus.PENDING);
            orderService.saveOrder(order);
            return vnPayConfig.getPayUrl() + "?" + queryData.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo URL thanh toán VNPay", e);
        }
    }

    public byte[] generatePaymentQRCode(String orderId, HttpServletRequest ipAddr) throws Exception {
        String paymentUrl = createPayment(orderId, ipAddr);
        return generateQRCodeImage(paymentUrl);
    }

    private static String hmacSHA512(String key, String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Chuyển bytes sang hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append("0");
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo chữ ký HMAC-SHA512", e);
        }
    }


    @Override
    public String handleIPN(Map<String, String> params) {
        String orderId = params.get("vnp_TxnRef");
        String transactionStatus = params.get("vnp_TransactionStatus"); // Trạng thái thanh toán do VNPay gui

        OrderEntity orderOpt = orderService.getOrder(orderId);
        if (!orderOpt.getOrderId().equals(orderId)) {
            return "Order not found";
        }

/// Kiểm tra mã giao dịch hợp lệ
        if ("00".equals(transactionStatus)) {
            //thanh toán thành công -> chuyên status
            orderOpt.setPaymentStatus(PaymentStatus.PAID);
            orderOpt.setOrderStatus(OrderStatus.COMPLETED);
            orderService.saveOrder(orderOpt);

            if (orderOpt.getPaymentMethod() != null && orderOpt.getPaymentMethod().equals(PaymentMethod.QR)) {
                NotificationEntity notification = new NotificationEntity();
                notification.setUser(orderOpt.getUser());
                notification.setOrder(orderOpt);
                notification.setMessage("Đặt hàng thành công");
                eventPublisher.publishEvent(new NotificationEvent(this, notification));
            }


            return orderId;
        } else {
            orderOpt.setPaymentStatus(PaymentStatus.FAILED);
            orderOpt.setOrderStatus(OrderStatus.CANCELED);
            orderService.saveOrder(orderOpt);
            return null;
        }
    }


//@Override
//public String handleIPN(Map<String, String> params) {
//    String orderId = params.get("vnp_TxnRef");
//    String transactionStatus = params.get("vnp_TransactionStatus");
//    String promotionCode = params.get("vnp_PromotionCode"); // Mã khuyến mãi từ VNPay
//    String promotionAmount = params.get("vnp_PromotionAmount"); // Số tiền được giảm
//
//    OrderEntity orderOpt = orderService.getOrder(orderId);
//    if (!orderOpt.getOrderId().equals(orderId)) {
//        return "Order not found";
//    }
//
//    if ("00".equals(transactionStatus)) {
//        // Xử lý khuyến mãi từ VNPay nếu có
//        if (promotionCode != null && promotionAmount != null) {
//            BigDecimal discountAmount = new BigDecimal(promotionAmount);
//            orderOpt.setDiscountAmount(discountAmount);
//            orderOpt.setVnpayPromoCode(promotionCode);
//            // Cập nhật lại tổng tiền sau khuyến mãi
//            orderOpt.setTotalAmount(orderOpt.getTotalPrice().subtract(discountAmount));
//        }
//
//        orderOpt.setPaymentStatus(PaymentStatus.PAID);
//        orderOpt.setOrderStatus(OrderStatus.COMPLETED);
//        orderService.saveOrder(orderOpt);
//
//        if(orderOpt.getPaymentMethod() != null && orderOpt.getPaymentMethod().equals(PaymentMethod.QR)) {
//            NotificationEntity notification = new NotificationEntity();
//            notification.setUser(orderOpt.getUser());
//            notification.setOrder(orderOpt);
//            notification.setMessage("Đặt hàng thành công" +
//                    (promotionCode != null ? " với mã khuyến mãi " + promotionCode : ""));
//            eventPublisher.publishEvent(new NotificationEvent(this, notification));
//        }
//
//        return orderId;
//    } else {
//        orderOpt.setPaymentStatus(PaymentStatus.FAILED);
//        orderOpt.setOrderStatus(OrderStatus.CANCELED);
//        orderService.saveOrder(orderOpt);
//        return null;
//    }
//}

//    // Method tạo mã QR
//    public static byte[] generateQRCodeImage(String text) throws Exception {
//        int width = 300; // Chiều rộng của mã QR
//        int height = 300; // Chiều cao của mã QR
//        String imageFormat = "PNG"; // Định dạng ảnh (PNG, JPEG, etc.)
//
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Mức độ sửa lỗi
//        hints.put(EncodeHintType.MARGIN, 1); // Khoảng cách giữa mã QR và biên
//
//        // Tạo mã QR dưới dạng BitMatrix
//        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//
//        // Chuyển BitMatrix thành byte[] (mảng byte của hình ảnh)
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, baos);
//        return baos.toByteArray();
//    }

    // Method tạo mã QR với thông tin thanh toán
    public static byte[] generateQRCodeImage(String text) throws Exception {
        int width = 300;
        int height = 300;
        String imageFormat = "PNG";

        // Tạo mã QR với định dạng VNPay
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Tăng độ chính xác
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // Hỗ trợ tiếng Việt
        hints.put(EncodeHintType.MARGIN, 2);
//        hints.put(EncodeHintType.QR_VERSION, 15); // Version cao hơn để chứa nhiều thông tin

        // Tạo VNPay Deep Link format
        String vnpayDeeplink = text;
        if (!text.startsWith("vnpay://")) {
            vnpayDeeplink = "vnpay://?url=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
        }

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                vnpayDeeplink,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, baos);
        return baos.toByteArray();
    }


}
