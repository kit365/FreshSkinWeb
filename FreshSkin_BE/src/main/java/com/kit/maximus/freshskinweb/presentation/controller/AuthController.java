package com.kit.maximus.freshskinweb.presentation.controller;
import com.kit.maximus.freshskinweb.business.service.AuthService;
import com.kit.maximus.freshskinweb.presentation.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.presentation.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.presentation.dto.response.ResponseAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.kit.maximus.freshskinweb.presentation.constants.ApiConstants.AUTH_BASE;

@RestController
@RequestMapping(AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseAPI<AuthenticationResponseDTO> checkLogin(@RequestBody AuthenticationRequest request) {
        String message = "Đăng nhập thành công";
        AuthenticationResponseDTO result = authService.authenticate(request);

        return ResponseAPI.<AuthenticationResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }


}
