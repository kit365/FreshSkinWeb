package com.kit.maximus.freshskinweb.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UpdateUserRequest implements Serializable {

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;
    Long role;
    String firstName;
    String lastName;
    String email;
    String phone;
    String avatar;
    MultipartFile newImg;
    String token;
    String address;
    String status;   // ACTIVE / INACTIVE
    String typeUser; // NORMAL / VIP
}

