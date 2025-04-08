package com.kit.maximus.freshskinweb.dto.request.user;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class UpdateUserPasswordRequest {


    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String oldPassword;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String confirmPassword;
}
