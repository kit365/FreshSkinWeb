package com.kit.maximus.freshskinweb.business.serviceimpl;
import com.kit.maximus.freshskinweb.business.service.AuthService;
import com.kit.maximus.freshskinweb.dataaccess.repository.UserRepository;
import com.kit.maximus.freshskinweb.presentation.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.presentation.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequest authenticationRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.generateToken(authentication);

            String refreshToken = tokenProvider.generateRefreshToken(authentication);


            return AuthenticationResponseDTO.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }


}
