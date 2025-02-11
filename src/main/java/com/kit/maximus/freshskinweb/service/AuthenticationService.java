package com.kit.maximus.freshskinweb.service;



import com.kit.maximus.freshskinweb.dto.request.AuthenticationRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {

    UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        UserEntity user = userRepository.findAllByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean result = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!result) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return AuthenticationResponseDTO.builder().token("20000").authenticated(result).build();
    }

}

