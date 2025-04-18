package com.kit.maximus.freshskinweb.service.users;


import com.kit.maximus.freshskinweb.dto.request.authentication.AuthenticationRequest;
import com.kit.maximus.freshskinweb.dto.request.authentication.IntrospectRequest;
import com.kit.maximus.freshskinweb.dto.request.productcomparison.ProductComparisonDTO;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.dto.response.productcomparison.ProductComparisonResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductComparisonEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.ProductComparisonRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements UserDetailsService {
    UserRepository userRepository;

    ProductComparisonRepository productComparisonRepository;

    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;


    public UserResponseDTO getUserByToken(String token) throws ParseException, JOSEException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedJWT.verify(jwsVerifier) || expirationDate.before(new Date())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String username = signedJWT.getJWTClaimsSet().getSubject();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(user);


        Long productComparisonId = userRepository.findProductComparisonIdByUserId(user.getUserID());

        if (productComparisonId != null) {
            ProductComparisonEntity productComparison = productComparisonRepository.findById(productComparisonId).orElse(null);
            ProductComparisonResponseDTO productComparisonDTO = new ProductComparisonResponseDTO();
            productComparisonDTO.setId(productComparisonId);
            List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
            if (productComparison.getProducts() != null) {
                productComparison.getProducts().forEach(product -> {
                    ProductResponseDTO productResponseDTO = new ProductResponseDTO();
                    productResponseDTO.setId(product.getId());
                    productResponseDTO.setTitle(product.getTitle());
                    productResponseDTO.setSlug(product.getSlug());
                    productResponseDTO.setThumbnail(product.getThumbnail());
                    ProductBrandResponse productBrandResponse = new ProductBrandResponse();
                    productBrandResponse.setTitle(product.getBrand().getTitle());
                    productResponseDTO.setBrand(productBrandResponse);
                    productResponseDTOS.add(productResponseDTO);
                });
            }
            productComparisonDTO.setProducts(productResponseDTOS);
            userResponseDTO.setProductComparisonId(productComparisonDTO);
        }


        return userResponseDTO;
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

        return AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    public AuthenticationResponseDTO authenticateAdmin(AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) {
        UserEntity user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        if(user.getRole() == null) {
            throw new AppException(ErrorCode.ROLE_ACCESS_DENIED);
        }


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

        return AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }


    public String generateToken(String username) {
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
