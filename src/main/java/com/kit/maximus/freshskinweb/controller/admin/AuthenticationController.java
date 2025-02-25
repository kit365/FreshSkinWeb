package com.kit.maximus.freshskinweb.controller.admin;



import com.kit.maximus.freshskinweb.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.dto.request.authentication.IntrospectRequest;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.IntrospectResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@CrossOrigin(origins = "*")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseAPI<AuthenticationResponseDTO> checkLogin(@RequestBody AuthenticationRequest request) {
        String message = "Login success";
        AuthenticationResponseDTO result = authenticationService.authenticate(request);

        return ResponseAPI.<AuthenticationResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }
    @PostMapping("/introspect")
    public ResponseAPI<IntrospectResponse> checkToken(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        String message = "Token check success";
        IntrospectResponse result = authenticationService.introspect(request);

        return ResponseAPI.<IntrospectResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();

    }




}
