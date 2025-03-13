package com.kit.maximus.freshskinweb.utils;

public enum ReactionType {
    LIKE("Đơn hàng đang chờ xử lý"),
    CANCELED("Đơn hàng đã bị hủy"),
    COMPLETED("Đơn hàng đã hoàn thành"),
    ;

    String message;

    ReactionType(String message) {
        this.message = message;
    }
}
