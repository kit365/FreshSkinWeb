package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
