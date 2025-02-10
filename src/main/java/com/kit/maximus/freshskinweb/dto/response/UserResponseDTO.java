package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


import java.io.Serializable;
import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class UserResponseDTO implements Serializable {

    Long id;
    String password;
    String username;
    String firstName;
    String lastName;
    String email;
    String phone;
    String avatar;
    String token;
    String address;
    String status;
    String typeUser;
    boolean deleted;
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;


}
