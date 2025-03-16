package com.kit.maximus.freshskinweb.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component

public class DashboardWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private DashboardService dashboardService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        System.out.println("Cổng dashboard trả data:");
        sendDataDashBoard(session);
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        System.out.println("📩 Kết nối cổng Dashboard mới từ:" + session.getId());
    }

    //xóa session khi đã đóng client
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("❌ Kết nối đóng từ cổng dashboard: " + session.getId());
    }

    public void sendDataDashBoard(WebSocketSession session) {

        CompletableFuture<String> totalRevenue = dashboardService.getTotalRevenue();
        CompletableFuture<Long> totalOrderCompleted = dashboardService.getOrderCompleted();
        CompletableFuture<Long> totalOrderPending = dashboardService.getOrderPending();
        CompletableFuture<Long> totalOrderCanceled = dashboardService.getOrderCanceled();
        CompletableFuture<Long> totalOrder = dashboardService.getTotalOrder();
        CompletableFuture<Long> totalBlogs = dashboardService.getTotalBlogs();
        CompletableFuture<Long> totalFeedback = dashboardService.getTotalReviews();
        CompletableFuture<Long> totalUser = dashboardService.getTotalUsers();
        CompletableFuture<Long> totalProduct = dashboardService.getTotalProducts();

        CompletableFuture.allOf(
                totalRevenue,
                totalOrderCompleted,
                totalOrderPending,
                totalOrderCanceled,
                totalOrder,
                totalBlogs,
                totalFeedback,
                totalUser,
                totalProduct
        ).join();


        Map<String, Object> data = new HashMap<>();
        try {
            data.put("totalOrder", totalOrder.get());
            data.put("totalOrderCompleted", totalOrderCompleted.get());
            data.put("totalOrderPending", totalOrderPending.get());
            data.put("totalOrderCanceled", totalOrderCanceled.get());
            data.put("totalRevenue", totalRevenue.get());
            data.put("totalBlogs", totalBlogs.get());
            data.put("totalFeedbacks", totalFeedback.get());
            data.put("totalUsers", totalUser.get());
            data.put("totalProducts", totalProduct.get());

            // Chuyển dữ liệu thành JSON và gửi qua WebSocket
            String JsonData = new ObjectMapper().writeValueAsString(data);
            session.sendMessage(new TextMessage(JsonData));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
