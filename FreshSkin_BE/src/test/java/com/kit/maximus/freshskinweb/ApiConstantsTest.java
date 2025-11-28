package com.kit.maximus.freshskinweb;

import com.kit.maximus.freshskinweb.presentation.constants.ApiConstants;

public class ApiConstantsTest {
    public static void main(String[] args) {
        System.out.println("=== API CONSTANTS TEST ===\n");

        System.out.println("API_BASE = \"" + ApiConstants.API_BASE + "\"");
        System.out.println("Expected: \"/api\"\n");

        System.out.println("AUTH_BASE = \"" + ApiConstants.AUTH_BASE + "\"");
        System.out.println("Expected: \"/api/auth\"\n");

        System.out.println("AUTH_PATH = \"" + ApiConstants.AUTH_PATH + "\"");
        System.out.println("Expected: \"/api/auth/**\"\n");

        System.out.println("WEBSOCKET_BASE = \"" + ApiConstants.WEBSOCKET_BASE + "\"");
        System.out.println("Expected: \"/ws\"\n");

        System.out.println("WEBSOCKET_PATH = \"" + ApiConstants.WEBSOCKET_PATH + "\"");
        System.out.println("Expected: \"/ws/**\"\n");

        System.out.println("VNPAY_BASE = \"" + ApiConstants.VNPAY_BASE + "\"");
        System.out.println("Expected: \"/api/vnpay\"\n");

        System.out.println("VNPAY_PATH = \"" + ApiConstants.VNPAY_PATH + "\"");
        System.out.println("Expected: \"/api/vnpay/**\"\n");

        System.out.println("WALLET_PAYMENT_CALLBACK = \"" + ApiConstants.WALLET_PAYMENT_CALLBACK + "\"");
        System.out.println("Expected: \"/api/wallet/payment-callback\"\n");

        System.out.println("USER_BASE = \"" + ApiConstants.USER_BASE + "\"");
        System.out.println("Expected: \"/api/user\"\n");

        System.out.println("PRODUCT_BASE = \"" + ApiConstants.PRODUCT_BASE + "\"");
        System.out.println("Expected: \"/api/products\"\n");

        System.out.println("ORDER_BASE = \"" + ApiConstants.ORDER_BASE + "\"");
        System.out.println("Expected: \"/api/orders\"\n");

        System.out.println("BLOG_BASE = \"" + ApiConstants.BLOG_BASE + "\"");
        System.out.println("Expected: \"/api/blogs\"\n");

        System.out.println("VOUCHER_BASE = \"" + ApiConstants.VOUCHER_BASE + "\"");
        System.out.println("Expected: \"/api/voucher\"\n");

        System.out.println("=== VERIFICATION ===");
        boolean allCorrect = true;

        if (!"/api".equals(ApiConstants.API_BASE)) {
            System.out.println("❌ API_BASE is WRONG!");
            allCorrect = false;
        }
        if (!"/api/auth".equals(ApiConstants.AUTH_BASE)) {
            System.out.println("❌ AUTH_BASE is WRONG!");
            allCorrect = false;
        }
        if (!"/api/auth/**".equals(ApiConstants.AUTH_PATH)) {
            System.out.println("❌ AUTH_PATH is WRONG!");
            allCorrect = false;
        }
        if (!"/ws".equals(ApiConstants.WEBSOCKET_BASE)) {
            System.out.println("❌ WEBSOCKET_BASE is WRONG!");
            allCorrect = false;
        }
        if (!"/ws/**".equals(ApiConstants.WEBSOCKET_PATH)) {
            System.out.println("❌ WEBSOCKET_PATH is WRONG!");
            allCorrect = false;
        }
        if (!"/api/vnpay".equals(ApiConstants.VNPAY_BASE)) {
            System.out.println("❌ VNPAY_BASE is WRONG!");
            allCorrect = false;
        }
        if (!"/api/vnpay/**".equals(ApiConstants.VNPAY_PATH)) {
            System.out.println("❌ VNPAY_PATH is WRONG!");
            allCorrect = false;
        }
        if (!"/api/wallet/payment-callback".equals(ApiConstants.WALLET_PAYMENT_CALLBACK)) {
            System.out.println("❌ WALLET_PAYMENT_CALLBACK is WRONG!");
            allCorrect = false;
        }

        if (allCorrect) {
            System.out.println("✅ TẤT CẢ CÁC CONSTANTS ĐỀU ĐÚNG!");
        } else {
            System.out.println("❌ CÓ LỖI TRONG CÁC CONSTANTS!");
        }
    }
}

