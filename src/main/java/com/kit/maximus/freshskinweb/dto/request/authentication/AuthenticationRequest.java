package com.kit.maximus.freshskinweb.dto.request.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class AuthenticationRequest implements Serializable {
    @NotNull(message = "USER_NOT_NULL")
    @NotBlank(message = "USER_NOT_BLANK")
    String username;
    @NotNull(message = "PASSWORD_NOT_NULL")
    @NotBlank(message = "PASSWORD_NOT_BLANK")
    String password;
}
