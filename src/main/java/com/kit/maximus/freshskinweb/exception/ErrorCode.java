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
    USER_NOT_NULL(400, "User Not Null"),
    USER_NOT_BLANK(400, "User Not Blank"),
    USERNAME_INVALID(400, "Username must be more than 5 and less than 20 characters"),
    PASSWORD_NOT_NULL(400, "Password Not Null"),
    PASSWORD_NOT_BLANK(400, "Password Not Blank"),
    PASSWORD_INVALID(400, "Password must be more than 5 and less than 20 characters"),
    USER_EXISTED(400, "User already existed"),
    SORT_DIRECTION_INVALID(400, "Sort Direction Invalid"),
    EMAIL_EXISTED(400, "Email already existed"),
    ;


    long code;
    String message;


    ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
