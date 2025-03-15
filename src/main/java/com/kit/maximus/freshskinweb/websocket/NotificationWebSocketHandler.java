package com.kit.maximus.freshskinweb.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


//nhận các thông báod9e63e gui về cho FE qua kênh socket đã đăng ký /ws/notify
@Slf4j
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    //    dùng currentmap để tránh lỗi race condition khi nhiều role out session
//    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final Set<WebSocketSession> sessions = new HashSet<>();
//    public NotificationWebSocketHandler() {
//        sessions.put("ADMIN", ConcurrentHashMap.newKeySet()); //tài khoản admin có thể nhận đc full thông báo
//        sessions.put("USER", ConcurrentHashMap.newKeySet());  //tài khoản user chỉ nhan thông báo cùả user
//        sessions.put("ORDER_MANAGER", ConcurrentHashMap.newKeySet()); //thông báo trang thái đơn hàng
//        sessions.put("PRODUCT_MANAGER", ConcurrentHashMap.newKeySet()); //thông báo trang thái đơn hàng
//    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("📩 Nhận tin nhắn từ client: " + payload);

    }


    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("📩 Kết nối Websocket mới từ:" + session.getId());
    }

    //xóa session khi đã đóng client
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("❌ Kết nối đóng: " + session.getId());
    }

    public void sendNotification(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                    System.out.println("📩 Đã gửi thông báo tới client: " + message);
                } catch (IOException e) {
                    log.error("❌ Lỗi gửi thông báo WebSocket: {}", e.getMessage());
                }
            } else {
                System.out.println("⚠️ Phiên WebSocket bị đóng trước khi gửi tin nhắn.");
            }
        }
    }


}
