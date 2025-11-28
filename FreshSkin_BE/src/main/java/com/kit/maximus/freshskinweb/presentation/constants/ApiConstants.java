package com.kit.maximus.freshskinweb.presentation.constants;

public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_BASE = "/api";

    public static final String AUTH_BASE = API_BASE + "/auth";
    public static final String AUTH_PATH = AUTH_BASE + "/**";

    public static final String WEBSOCKET_BASE = "/ws";
    public static final String WEBSOCKET_PATH = WEBSOCKET_BASE + "/**";

    public static final String WALLET_PAYMENT_CALLBACK = API_BASE + "/wallet/payment-callback";

    public static final String VNPAY_BASE = API_BASE + "/vnpay";
    public static final String VNPAY_PATH = VNPAY_BASE + "/**";

    public static final String USER_BASE = API_BASE + "/user";

    public static final String PRODUCT_BASE = API_BASE + "/products";

    public static final String ORDER_BASE = API_BASE + "/orders";

    public static final String BLOG_BASE = API_BASE + "/blogs";

    public static final String VOUCHER_BASE = API_BASE + "/voucher";
}

