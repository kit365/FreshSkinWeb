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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class CreateUserRequest implements Serializable {

    @NotBlank(message = "USERNAME_NOT_BLANK")
    @Size(min = 5, max = 20, message = "USERNAME_INVALID")
    String username;

    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!]*$", message = "PASSWORD_REGEXP_INVALID")
    String password;

    @NotNull(message = "ROLE_ID_NOT_BLANK")
    Long roleId;

    @NotBlank(message = "FIRSTNAME_NOT_BLANK")
    @Size(min = 2, max = 50, message = "FIRSTNAME_INVALID")
    String firstName;

    @NotBlank(message = "LASTNAME_NOT_BLANK")
    @Size(min = 2, max = 50, message = "LASTNAME_INVALID")
    String lastName;

    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "PHONE_NUMBER_NOT_BLANK")
    @Pattern(regexp = "^\\d{10}$", message = "PHONE_NUMBER_INVALID")
    String phone;

    @NotNull(message = "AVATAR_NOT_NULL")
    @NotBlank(message = "AVATAR_NOT_BLANK")
    MultipartFile avatar;

    @NotBlank(message = "ADDRESS_NOT_BLANK")
    @Size(min = 5, max = 255, message = "ADDRESS_INVALID")
    String address;

    @NotBlank(message = "STATUS_NOT_BLANK")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "STATUS_INVALID")
    String status;

    @NotBlank(message = "TYPE_USER_NOT_BLANK")
    @Pattern(regexp = "NORMAL|VIP", message = "TYPE_USER_INVALID")
    String typeUser;

}


