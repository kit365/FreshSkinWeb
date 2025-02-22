package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class UserResponseDTO implements Serializable {

    Long id;
    RoleResponseDTO roleId;
    String password;
    String username;
    String firstName;
    String lastName;

    List<OrderEntity> orders;
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
