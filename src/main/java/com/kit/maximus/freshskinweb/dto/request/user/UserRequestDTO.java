package com.kit.maximus.freshskinweb.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class UserRequestDTO implements Serializable {

    Long id;
    @NotNull(message = "username not null")
    String username;
    @NotNull(message = "password not null")
    String password;

    String firstName;
    String lastName;

    String email;
    String phone;
    String avatar;
    String token;
    String address;
    String status;   // ACTIVE / INACTIVE
    String typeUser; // NORMAL / VIP

}
