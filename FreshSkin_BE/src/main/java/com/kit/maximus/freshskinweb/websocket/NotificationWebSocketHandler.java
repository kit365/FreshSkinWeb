package com.kit.maximus.freshskinweb.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private NotificationService notificationService;

    // Lưu danh sách session theo roleId
    private final Map<Long, Set<WebSocketSession>> roleSessions = new ConcurrentHashMap<>();

    // Lưu roleId của từng session. Dùng để biết một session thuộc về role nào.
    private final Map<WebSocketSession, Long> sessionRoles = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        log.info("📩 Kết nối WebSocket mới từ: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            Long roleId = Long.parseLong(message.getPayload());

            sessionRoles.put(session, roleId);
            roleSessions.computeIfAbsent(roleId, k -> ConcurrentHashMap.newKeySet()).add(session);

            // 🔥 Gửi tin nhắn chưa đọc ngay sau khi nhận roleId từ FE
            long unreadCount = notificationService.countMessageFeedback(roleId);
            Map<String, Object> result = new ConcurrentHashMap<>();
            result.put("unreadCount", unreadCount);

            ObjectMapper objectMapper = new ObjectMapper();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));

        } catch (NumberFormatException e) {
            log.error("❌ Lỗi parse roleID từ WebSocket message: {}", message.getPayload());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long roleId = sessionRoles.remove(session);
        if (roleId != null) {
            roleSessions.getOrDefault(roleId, ConcurrentHashMap.newKeySet()).remove(session);
        }
        log.info("❌ Kết nối đóng: {}", session.getId());
    }

    // Gửi thông báo đến tất cả session có cùng roleId
    public void sendNotification(String message, long roleId) throws IOException {
        long unreadCount = notificationService.countMessageFeedback(roleId);

        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("unreadCount", unreadCount);
        result.put("message", message);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(result);
        log.info("📤 JSON gửi đi: {}", jsonData);

        Set<WebSocketSession> sessions = roleSessions.getOrDefault(roleId, ConcurrentHashMap.newKeySet());
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonData));
                    log.info("✅ Gửi thành công tới session: {}", session.getId());
                } catch (IOException e) {
                    log.error("❌ Lỗi gửi thông báo WebSocket: {}", e.getMessage());
                }
            } else {
                log.warn("⚠️ Phiên WebSocket {} bị đóng trước khi gửi tin nhắn.", session.getId());
            }
        }
    }
}
