package com.kit.maximus.freshskinweb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.users.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

      AuthenticationService authenticationService;
      ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String token = authenticationService.generateToken(email);

        AuthenticationResponseDTO authResponse = AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(true)
                .build();

        ResponseAPI<AuthenticationResponseDTO> apiResponse = ResponseAPI.<AuthenticationResponseDTO>builder()
                .data(authResponse)
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}