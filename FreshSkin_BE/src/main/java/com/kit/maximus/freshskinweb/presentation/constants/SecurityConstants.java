package com.kit.maximus.freshskinweb.presentation.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String API_DOCS_PATH = "/api-docs/**";
    public static final String SWAGGER_UI_PATH = "/swagger-ui/**";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String V3_API_DOCS_PATH = "/v3/api-docs/**";

    public static String[] getPublicEndpoints() {
        return new String[] {
                ApiConstants.AUTH_PATH,
                ApiConstants.WALLET_PAYMENT_CALLBACK,
                ApiConstants.VNPAY_PATH,
                ApiConstants.WEBSOCKET_PATH,
                API_DOCS_PATH,
                SWAGGER_UI_PATH,
                SWAGGER_UI_HTML,
                V3_API_DOCS_PATH
        };
    }
}

