package com.kit.maximus.freshskinweb.service;



import com.kit.maximus.freshskinweb.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.dto.request.authentication.IntrospectRequest;
import com.kit.maximus.freshskinweb.dto.response.AuthenticationResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.IntrospectResponse;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements UserDetailsService {
    UserRepository userRepository;

    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    public UserResponseDTO getUserByToken(String token) throws ParseException, JOSEException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verify = signedJWT.verify(jwsVerifier);
        if(verify && expirationDate.after(new Date())) {
            String username = signedJWT.getJWTClaimsSet().getSubject();
            UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            return userMapper.toUserResponseDTO(user);
        }
        return null;
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verify = signedJWT.verify(jwsVerifier);
        return IntrospectResponse.builder().valid(verify && expirationDate.after(new Date())).build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) {
        UserEntity user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }
        if (user.getStatus().equals(Status.INACTIVE)) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        // Generate JWT Token
        String token = generateToken(authenticationRequest.getUsername());

        // Tạo cookie chứa token
        Cookie cookie = new Cookie("token", token);
        cookie.setDomain("freshskinweb.onrender.com");
//        cookie.setDomain("localhost");
        cookie.setPath("/"); // Áp dụng cho toàn bộ trang web
        cookie.setHttpOnly(true); // Chỉ backend truy cập, bảo mật hơn
        cookie.setSecure(true); // Chỉ hoạt động trên HTTPS
        cookie.setMaxAge(60 * 60 * 24); // Hết hạn sau 1 ngày
        cookie.setAttribute("SameSite", "None"); // Quan trọng khi frontend khác origin

        // Thêm cookie vào response
        if (request.getServerName().equals("localhost")) {
            response.setHeader("Set-Cookie",
                    "token=" + token + "; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=86400");
        } else {
            response.setHeader("Set-Cookie",
                    "token=" + token + "; Path=/; HttpOnly; Secure; SameSite=None; Domain=freshskinweb.onrender.com; Max-Age=86400");
        }

        response.addCookie(cookie);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }


    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setDomain("freshskinweb.onrender.com");
//        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
    }



    private String generateToken(String username) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("FreshSkinWeb.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("username", username)
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can not `generate token` ", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
