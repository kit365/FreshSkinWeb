package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.forgot_password.ForgotPasswordRequest;
import com.kit.maximus.freshskinweb.entity.ForgotPasswordEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ForgotPasswordMapper;
import com.kit.maximus.freshskinweb.repository.ForgotPasswordRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordService {
    ForgotPasswordRepository forgotPasswordRepository;
    UserRepository userRepository;
    EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 1;

    public boolean createForgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email);
        try {
            if (email != null) {
                if (user != null) {
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
                }
                throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            return false;
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

    public boolean verifyOtp(String email, String otp) {
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

        return true;
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
