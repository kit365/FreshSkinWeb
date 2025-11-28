package com.kit.maximus.freshskinweb.business.service;

import com.kit.maximus.freshskinweb.presentation.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.presentation.dto.response.AuthenticationResponseDTO;

public interface AuthService {

    AuthenticationResponseDTO authenticate(AuthenticationRequest authenticationRequest);

}
