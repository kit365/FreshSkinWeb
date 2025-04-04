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

    String password;

    Long role;

    String firstName;

    String lastName;

    String email;

    String phone;

    List<MultipartFile> avatar;

    String address;

    String status;

    String typeUser;

}
