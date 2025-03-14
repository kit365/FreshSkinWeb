//package com.kit.maximus.freshskinweb.dto.request.user;
//
//import com.kit.maximus.freshskinweb.entity.OrderEntity;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.ToString;
//import lombok.experimental.FieldDefaults;
//
//import java.io.Serializable;
//import java.util.List;
//
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Getter
//@ToString
//@Builder
//public class CreateUserRequest implements Serializable {
//    @NotNull(message = "USER_NOT_NULL")
//    @NotBlank(message = "USER_NOT_BLANK")
//    @Size(min = 5, max = 20, message = "USERNAME_INVALID")
//    String username;
//    @NotNull(message = "PASSWORD_NOT_NULL")
//    @NotBlank(message = "PASSWORD_NOT_BLANK")
//    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
//    String password;
//    Long roleId;
//    String firstName;
//    String lastName;
//    String email;
//    String phone;
//    List<OrderEntity> orders;
//    String avatar;
//    String token;
//    String address;
//    String status;   // ACTIVE / INACTIVE
//    String typeUser; // NORMAL / VIP
//}



package com.kit.maximus.freshskinweb.dto.request.user;

import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest implements Serializable {

    String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!]*$", message = "PASSWORD_REGEXP_INVALID")
    String password;

    Long role;

    String firstName;

    String lastName;

    String email;

    String phone;

    MultipartFile avatar;

    String address;

    String status;

    String typeUser;

}


