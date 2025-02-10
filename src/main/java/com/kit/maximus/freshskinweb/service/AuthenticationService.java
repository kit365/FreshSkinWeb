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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        UserEntity user = userRepository.findAllByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
       boolean result = passwordEncoder.matches(request.getPassword(), user.getPassword());
//       if(result){
//           String token = UUID.randomUUID().toString();
//           String message = "Login Success!";
//           return new AuthenticationResponseDTO(token,message);
//       } else {
//           String message = "Wrong password!";
//           return new AuthenticationResponseDTO(message);
//       }
        if(!result) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return AuthenticationResponseDTO.builder().token("1000").authenticated(result).build();
    }

}
