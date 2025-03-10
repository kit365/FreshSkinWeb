package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.Cookie;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponseDTO implements Serializable {
    String token;
    boolean authenticated;

}
