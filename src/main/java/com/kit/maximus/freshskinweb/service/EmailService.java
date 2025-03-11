package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.SettingRepository;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender mailSender;
    TemplateEngine templateEngine;
    OrderRepository orderRepository;
    SettingRepository settingRepository;

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
}