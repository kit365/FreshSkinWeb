package com.kit.maximus.freshskinweb.utils;

public enum PaymentStatus {
    PENDING("Chờ thanh toán"),
    PAID("Đã thanh toán thành công"),
    FAILED("Thanh toán thất bại"),
    CANCELED("Thanh toán bị hủy"),
    REFUNDED("Hoàn tiền");

    private final String message;

    PaymentStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
