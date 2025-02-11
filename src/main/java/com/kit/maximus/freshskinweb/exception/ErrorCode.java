package com.kit.maximus.freshskinweb.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception"),
    USER_NOT_FOUND(404, "User Not Found"),
    UNAUTHENTICATED(401, "Unauthenticated"),
    KEY_INVALID(400, "Key Invalid"),
    USER_ALREADY_EXISTS(400, "User Already Exists"),
    SORT_DIRECTION_INVALID(400, "Sort Direction Invalid(asc | desc)"),
    ;


    long code;
    String message;


    ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
