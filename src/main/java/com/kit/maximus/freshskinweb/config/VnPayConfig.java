package com.kit.maximus.freshskinweb.config;

import jakarta.annotation.PostConstruct;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VnPayConfig {

    @Value("${vnpay.returnurl:}")
    private String returnUrlFromProperties;

    private final Dotenv dotenv;

    public VnPayConfig() {
        // Load .env nếu có (dùng cho local)
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = dotenv.get(key);
        }
        return value != null ? value : defaultValue;
    }

    public String getTmnCode() {
        return getEnvOrDefault("VNPAY_TMN_CODE", "DEFAULT_TMN_CODE");
    }

    public String getHashSecret() {
        return getEnvOrDefault("VNPAY_HASH_SECRET", "DEFAULT_SECRET");
    }

    public String getPayUrl() {
        return getEnvOrDefault("VNPAY_PAY_URL", "https://sandbox.vnpayment.vn/paymentv2");
    }

    //nếu là localhost -> doc url tren properties, con la sever thi doc tu env(thong qua bien moi truong)
    public String getReturnUrl() {
        return getEnvOrDefault("VNPAY_RETURN_URL", returnUrlFromProperties != null ? returnUrlFromProperties : "http://localhost:8080/api/vnpay/payment-return");
    }

    @PostConstruct
    public void printConfig() {
        System.out.println("🔍 VNPay Config:");
        System.out.println("VNPAY_TMN_CODE: " + blurText(getTmnCode()));
        System.out.println("VNPAY_HASH_SECRET: " + (getHashSecret() != null ? "LOADED ✅" : "NOT FOUND ❌"));
        System.out.println("VNPAY_PAY_URL: " + blurText(getPayUrl()));
        System.out.println("VNPAY_RETURN_URL: " + getReturnUrl());
    }

    private String blurText(String text) {
        if (text == null || text.length() < 6) return "******"; // Nếu quá ngắn, che hết luôn
        return text.substring(0, 3) + "*****" + text.substring(text.length() - 3);
    }
}
