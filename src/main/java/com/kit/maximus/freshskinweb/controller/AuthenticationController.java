package com.kit.maximus.freshskinweb.controller;



import com.kit.maximus.freshskinweb.dto.request.AuthenticationRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseAPI<AuthenticationResponseDTO> checkLogin(@RequestBody AuthenticationRequestDTO request) {
        String message = "Login success";
        AuthenticationResponseDTO result = authenticationService.authenticate(request);

        return ResponseAPI.<AuthenticationResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }




}
