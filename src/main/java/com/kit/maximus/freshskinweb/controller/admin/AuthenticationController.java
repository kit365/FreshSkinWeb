package com.kit.maximus.freshskinweb.controller.admin;



import com.kit.maximus.freshskinweb.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.dto.request.authentication.IntrospectRequest;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.IntrospectResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.service.users.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

//@CrossOrigin(origins = "*")
@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseAPI<AuthenticationResponseDTO> checkLogin(@RequestBody AuthenticationRequest request, HttpServletResponse response, HttpServletRequest httpRequest ) {
        log.info(httpRequest.getRequestURI());
        String message = "Đăng nhập thành công";
        AuthenticationResponseDTO result = authenticationService.authenticate(request, response,httpRequest);

        return ResponseAPI.<AuthenticationResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }

    @PostMapping("/login-admin")
    public ResponseAPI<AuthenticationResponseDTO> checkLoginAdmin(@RequestBody AuthenticationRequest request, HttpServletResponse response, HttpServletRequest httpRequest ) {
        log.info(httpRequest.getRequestURI());
        String message = "Đăng nhập thành công";
        AuthenticationResponseDTO result = authenticationService.authenticateAdmin(request, response,httpRequest);

        return ResponseAPI.<AuthenticationResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }

    @PostMapping("/logout")
    public ResponseAPI<String> logout(HttpServletResponse response) {
            return ResponseAPI.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Đăng xuất thành công")
                    .build();

    }

    @PostMapping("/introspect")
    public ResponseAPI<IntrospectResponse> checkToken(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        String message = "Kiểm tra token thành công";
        IntrospectResponse result = authenticationService.introspect(request);

        return ResponseAPI.<IntrospectResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }

    @PostMapping("/getUser")
    public ResponseAPI<UserResponseDTO> getUser(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        String message = "Lấy dữ liệu user thành công";
        UserResponseDTO result = authenticationService.getUserByToken(request.getToken());
        return ResponseAPI.<UserResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }




}
