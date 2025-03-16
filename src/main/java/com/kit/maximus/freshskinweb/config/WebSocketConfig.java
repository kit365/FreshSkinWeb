package com.kit.maximus.freshskinweb.config;

import com.kit.maximus.freshskinweb.service.notification.NotificationListener;
import com.kit.maximus.freshskinweb.websocket.NotificationWebSocketHandler;
import com.sun.nio.sctp.NotificationHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
//định nghĩa các channel
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NotificationWebSocketHandler(), "/ws/notify")
                .setAllowedOrigins("https://project-swp391-n9j6.onrender.com", "http://localhost:8080", "http://localhost:3000","https://freshskinweb.onrender.com");
    }
}
