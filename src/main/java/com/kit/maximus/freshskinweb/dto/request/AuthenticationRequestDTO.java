package com.kit.maximus.freshskinweb.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class AuthenticationRequestDTO implements Serializable {
    String username;
    String password;
}
