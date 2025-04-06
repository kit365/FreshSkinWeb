package com.kit.maximus.freshskinweb.config;

import com.kit.maximus.freshskinweb.websocket.DashboardWebSocketHandler;
import com.kit.maximus.freshskinweb.websocket.NotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket

//định nghĩa các channel
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final DashboardWebSocketHandler dashboardWebSocketHandler;

    public WebSocketConfig(NotificationWebSocketHandler notificationWebSocketHandler, DashboardWebSocketHandler dashboardWebSocketHandler) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
        this.dashboardWebSocketHandler = dashboardWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationWebSocketHandler, "/ws/notify")
                .setAllowedOrigins("https://project-swp391-n9j6.onrender.com", "http://localhost:8080", "http://localhost:3000", "https://freshskinweb.onrender.com");

        registry.addHandler(dashboardWebSocketHandler, "/ws/dashboard")
                .setAllowedOrigins("https://project-swp391-n9j6.onrender.com", "http://localhost:8080", "http://localhost:3000", "https://freshskinweb.onrender.com");
    }
}
