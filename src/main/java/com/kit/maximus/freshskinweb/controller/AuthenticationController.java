package com.kit.maximus.freshskinweb.controller;



import com.kit.maximus.freshskinweb.dto.request.AuthenticationRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
        AuthenticationResponseDTO result = authenticationService.authenticate(request);

        return ResponseAPI.<AuthenticationResponseDTO>builder().code(1000).data(result).build();

    }




}
