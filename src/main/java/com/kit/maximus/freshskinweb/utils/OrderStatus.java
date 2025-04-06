package com.kit.maximus.freshskinweb.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum OrderStatus {
    PENDING("Đơn hàng đang chờ xử lý"),
    DELIVERING("Đơn hàng đang giao"),
    CANCELED("Đơn hàng đã bị hủy"),
    COMPLETED("Đơn hàng đã hoàn thành"),
    ;

     String message;

    OrderStatus(String message) {
        this.message = message;
    }

}
