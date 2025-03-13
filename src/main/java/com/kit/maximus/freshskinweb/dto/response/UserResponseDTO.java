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
@With
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO implements Serializable {

    Long userID;
    RoleResponseDTO role;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String password;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String username;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String firstName;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<OrderEntity> orders;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String email;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String phone;
    String avatar;
    String token;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String address;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String status;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String typeUser;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean deleted;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<UserDiscountUsageResponse> userDiscountUsageResponses;

}
