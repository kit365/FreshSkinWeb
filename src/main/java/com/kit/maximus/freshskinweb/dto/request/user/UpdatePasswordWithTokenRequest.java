package com.kit.maximus.freshskinweb.dto.request.user;

import jakarta.validation.constraints.NotBlank;
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
public class UpdatePasswordWithTokenRequest {

    @NotBlank(message = "TOKEN_REQUIRED")
    String token;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String newPassword;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String confirmPassword;
}
