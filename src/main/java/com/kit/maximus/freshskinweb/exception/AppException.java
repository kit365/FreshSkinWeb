package com.kit.maximus.freshskinweb.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException {
    final ErrorCode errorCode;
    String customMessage;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage); // có thể dùng để trả ra message tùy chỉnh
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    @JsonIgnore
    public String getMessageToShow() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}
