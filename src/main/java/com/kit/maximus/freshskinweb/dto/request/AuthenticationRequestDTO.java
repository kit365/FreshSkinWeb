package com.kit.maximus.freshskinweb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class AuthenticationRequestDTO implements Serializable {
    @NotNull(message = "USER_NOT_NULL")
    @NotBlank(message = "USER_NOT_BLANK")
    String username;
    @NotNull(message = "PASSWORD_NOT_NULL")
    @NotBlank(message = "PASSWORD_NOT_BLANK")
    String password;
}
