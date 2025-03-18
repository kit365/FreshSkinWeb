package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.forgot_password.ForgotPasswordRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.users.ForgotPasswordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/forgot-password")
@Slf4j
public class ForgotPasswordController {
    ForgotPasswordService forgotPasswordService;

    @PostMapping("/request")
    public ResponseAPI<Boolean> requestForgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Request to create forgot password for email: {}", request.getEmail());
        boolean result = forgotPasswordService.createForgotPassword(request.getEmail());

        String message = result ?
                "Mã OTP đã được gửi đến email của bạn" :
                "Không thể gửi mã OTP";

        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

    @PostMapping("/verify")
    public ResponseAPI<Boolean> verifyOtp(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Request to verify OTP for email: {}", request.getEmail());
        boolean result = forgotPasswordService.verifyOtp(request.getEmail(), request.getOTP());

        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Xác thực OTP thành công")
                .data(result)
                .build();
    }

    @PostMapping("/resend")
    public ResponseAPI<Boolean> resendOtp(@RequestBody ForgotPasswordRequest request) {
        log.info("Request to resend OTP for email: {}", request.getEmail());
        boolean result = forgotPasswordService.createForgotPassword(request.getEmail());

        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Đã gửi lại mã OTP mới")
                .data(result)
                .build();
    }

}
