package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.config.MailConfig;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.SettingRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailService {
    final JavaMailSender mailSender;
    final TemplateEngine templateEngine;
    final OrderRepository orderRepository;
    final SettingRepository settingRepository;
    final UserRepository userRepository;

    MailConfig mailConfig;

    final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    final String sender = dotenv.get("MAIL_USERNAME");
    final String personal = dotenv.get("MAIL_PERSONAL");

    private Map<OrderStatus, String> getOrderStatusMap() {
        Map<OrderStatus, String> statusMap = new HashMap<>();
        statusMap.put(OrderStatus.PENDING, "Chờ xác nhận");
        statusMap.put(OrderStatus.COMPLETED, "Hoàn thành");
        statusMap.put(OrderStatus.CANCELED, "Đã hủy");
        return statusMap;
    }

    @Async
    @Transactional(readOnly = true)
    public void sendOrderConfirmationEmail(String orderId) {
        try {
            OrderEntity order = orderRepository.findByOrderIdWithDetails(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            SettingEntity setting = settingRepository.findById(1L)
                    .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));


            UserEntity user = order.getUser();
            if (user == null || user.getEmail() == null || order.getEmail() == null) {
                throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getEmail());
            helper.setSubject("Xác nhận đơn hàng #" + order.getOrderId());

            // Add logo as inline image
            ClassPathResource logoResource = new ClassPathResource("static/images/img.png");
            helper.addInline("logo", logoResource);

            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("setting", setting);
            context.setVariable("statusMap", getOrderStatusMap());

            // Convert Timestamp to formatted String
            String formattedDate = "";
            if (order.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formattedDate = sdf.format(order.getUpdatedAt());
            }
            context.setVariable("createdAtFormatted", formattedDate);

            String content = templateEngine.process("order-confirmation", context);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            SettingEntity settingEntity = settingRepository.findById(1L)
                    .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));

            String websiteName = settingEntity.getWebsiteName();

            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("expireTime", "1 phút");
            context.setVariable("websiteName", websiteName); // Thêm tên website

            String content = templateEngine.process("forgot-password", context);

            helper.setFrom(new InternetAddress(sender, personal));
            helper.setTo(to);
            helper.setSubject("Mã xác thực OTP");
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Sent OTP email to: {}", to);
        } catch (Exception e) {
            log.error("Error sending OTP email: {}", e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }
}