package com.kit.maximus.freshskinweb.service.users;

import com.kit.maximus.freshskinweb.entity.ForgotPasswordEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.repository.ForgotPasswordRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordService {

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    ForgotPasswordRepository forgotPasswordRepository;
    UserRepository userRepository;
    EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 1;

    public boolean createForgotPassword(String email) {
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_EMAIL);
        }

        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }

        try {
            // Vô hiệu hóa các OTP cũ
            deactivateOldOtps(email);

            // Tạo OTP mới
            String otp = generateOtp();
            LocalDateTime now = LocalDateTime.now();

            ForgotPasswordEntity forgotPasswordEntity = ForgotPasswordEntity.builder()
                    .email(email)
                    .OTP(otp)
                    .createdAt(now)
                    .expiredAt(now.plusMinutes(OTP_EXPIRATION_MINUTES))
                    .status(Status.ACTIVE)
                    .build();

            forgotPasswordRepository.save(forgotPasswordEntity);
            emailService.sendOtpEmail(email, otp);

            return true;
        } catch (Exception e) {
            log.error("Error in createForgotPassword: ", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // Vô hiệu hóa các OTP cũ
    private void deactivateOldOtps(String email) {
        List<ForgotPasswordEntity> oldOtps = forgotPasswordRepository.findByEmailAndStatus(email, Status.ACTIVE);
        oldOtps.forEach(otp -> {
            otp.setStatus(Status.INACTIVE);
            forgotPasswordRepository.save(otp);
        });
    }

    public String verifyOtp(String email, String otp) {
        // Kiểm tra xem OTP có tồn tại không
        ForgotPasswordEntity forgotPassword = forgotPasswordRepository
                .findByEmailAndOTPAndStatus(email, otp, Status.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        // Kiểm tra xem OTP đã hết hạn chưa
        if (forgotPassword.isExpired()) {
            forgotPassword.setStatus(Status.INACTIVE);
            forgotPassword.setDeleted(true);
            forgotPasswordRepository.save(forgotPassword);
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        // Vô hiệu hóa OTP sau khi xác thực thành công
        forgotPassword.setStatus(Status.INACTIVE);
        forgotPassword.setDeleted(true);
        forgotPasswordRepository.save(forgotPassword);

        // Generate token for the user
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }

        String token = generateToken(user.getUsername());
        return token;
    }

    // Method to generate token for the user
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
    // Tạo mã OTP tự động
    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Sinh số từ 0-9
        }
        return otp.toString();
    }
}
