//package com.kit.maximus.freshskinweb.websocket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
//import com.kit.maximus.freshskinweb.service.DashboardService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//@Slf4j
//@Component
//public class DashboardWebSocketHandler extends TextWebSocketHandler {
//
//    @Autowired
//    private DashboardService dashboardService;
//
//    private List<WebSocketSession> sessions = new ArrayList<>();
//    // Dữ liệu cũ để so sánh để tránh spam quá niều
//    private Map<String, Object> previousData = new HashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        System.out.println("📩 Kết nối cổng Dashboard mới từ:" + session.getId());
//        sessions.add(session);
//
//        //gửi dữ liệu khi connect
//        sendDataDashBoard(session);
//
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        System.out.println("❌ Kết nối đóng từ cổng dashboard: " + session.getId());
//        sessions.remove(session);
//        previousData.clear();
//    }
//
/// /     Gửi dữ liệu tự động mỗi 5s
//    @Scheduled(fixedRate = 5000) // Gửi mỗi 5s)
//    public void sendPeriodicDataToClients() {
//        for (WebSocketSession session : sessions) {
//                sendDataDashBoard(session);
//        }
//    }
//
//    public void sendDataDashBoard(WebSocketSession session) {
//        // Lấy dữ liệu mới từ các service
//        CompletableFuture<String> totalRevenue = dashboardService.getTotalRevenue();
//        CompletableFuture<Long> totalOrderCompleted = dashboardService.getOrderCompleted();
//        CompletableFuture<Long> totalOrderPending = dashboardService.getOrderPending();
//        CompletableFuture<Long> totalOrderCanceled = dashboardService.getOrderCanceled();
//        CompletableFuture<Long> totalOrder = dashboardService.getTotalOrder();
//        CompletableFuture<Long> totalBlogs = dashboardService.getTotalBlogs();
//        CompletableFuture<Long> totalFeedback = dashboardService.getTotalReviews();
//        CompletableFuture<Long> totalUser = dashboardService.getTotalUsers();
//        CompletableFuture<Long> totalProduct = dashboardService.getTotalProducts();
//        CompletableFuture<List<ProductResponseDTO>> top10ProductSelling = dashboardService.getTop10SellingProducts();
//
//        CompletableFuture.allOf(
//                totalRevenue,
//                totalOrderCompleted,
//                totalOrderPending,
//                totalOrderCanceled,
//                totalOrder,
//                totalBlogs,
//                totalFeedback,
//                totalUser,
//                totalProduct,
//                top10ProductSelling
//        ).join();
//
//        Map<String, Object> data = new HashMap<>();
//        try {
//            // Đặt giá trị vào map
//            data.put("totalOrder", totalOrder.get());
//            data.put("totalOrderCompleted", totalOrderCompleted.get());
//            data.put("totalOrderPending", totalOrderPending.get());
//            data.put("totalOrderCanceled", totalOrderCanceled.get());
//            data.put("totalRevenue", totalRevenue.get());
//            data.put("totalBlogs", totalBlogs.get());
//            data.put("totalFeedbacks", totalFeedback.get());
//            data.put("totalUsers", totalUser.get());
//            data.put("totalProducts", totalProduct.get());
//            data.put("top10ProductSelling", top10ProductSelling.get());
//
//            // Chuyển dữ liệu mới và cũ thành JSON
//            ObjectMapper objectMapper = new ObjectMapper();
//            String previousJson = objectMapper.writeValueAsString(previousData);
//            String newJson = objectMapper.writeValueAsString(data);
//
//            // Kiểm tra xem dữ liệu có thay đổi không
//            if (!previousJson.equals(newJson)) {
//                // Nếu có thay đổi, gửi qua WebSocket
//                String jsonData = new ObjectMapper().writeValueAsString(data);
//                session.sendMessage(new TextMessage(jsonData));
//
//                // Cập nhật dữ liệu cũ
//                previousData = new HashMap<>(data);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//
//}


package com.kit.maximus.freshskinweb.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class DashboardWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private DashboardService dashboardService;

    private Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // Dữ liệu cũ để so sánh để tránh spam quá niều
    private Map<String, Object> previousData = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("📩 Kết nối cổng Dashboard mới từ: " + session.getId());
        sessions.add(session);
        sendDataDashBoard(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("❌ Kết nối đóng từ cổng dashboard: " + session.getId());
        sessions.remove(session);
        previousData.clear();
    }

    //     Gửi dữ liệu tự động mỗi 5s
    @Scheduled(fixedRate = 5000) // Gửi mỗi 5s)
    public void sendPeriodicDataToClients() {

        try {
            sessions.removeIf(session -> !session.isOpen());
            for (WebSocketSession session : sessions) {
                sendDataDashBoard(session);
            }
        } catch (Exception e) {
            log.error("loi gui data");
            throw new RuntimeException(e);
        }

    }

    public void sendDataDashBoard(WebSocketSession session) {
        // Lấy dữ liệu mới từ các service
        CompletableFuture<String> totalRevenue = dashboardService.getTotalRevenue();
        CompletableFuture<Long> totalOrderCompleted = dashboardService.getOrderCompleted();
        CompletableFuture<Long> totalOrderPending = dashboardService.getOrderPending();
        CompletableFuture<Long> totalOrderCanceled = dashboardService.getOrderCanceled();
        CompletableFuture<Long> totalOrder = dashboardService.getTotalOrder();
        CompletableFuture<Long> totalBlogs = dashboardService.getTotalBlogs();
        CompletableFuture<Long> totalFeedback = dashboardService.getTotalReviews();
        CompletableFuture<Long> totalUser = dashboardService.getTotalUsers();
        CompletableFuture<Long> totalProduct = dashboardService.getTotalProducts();
        CompletableFuture<List<ProductResponseDTO>> top10ProductSelling = dashboardService.getTop10SellingProducts();

        CompletableFuture.allOf(
                totalRevenue,
                totalOrderCompleted,
                totalOrderPending,
                totalOrderCanceled,
                totalOrder,
                totalBlogs,
                totalFeedback,
                totalUser,
                totalProduct,
                top10ProductSelling
        ).join();

        Map<String, Object> data = new HashMap<>();
        try {
            // Đặt giá trị vào map
            data.put("totalOrder", totalOrder.get());
            data.put("totalOrderCompleted", totalOrderCompleted.get());
            data.put("totalOrderPending", totalOrderPending.get());
            data.put("totalOrderCanceled", totalOrderCanceled.get());
            data.put("totalRevenue", totalRevenue.get());
            data.put("totalBlogs", totalBlogs.get());
            data.put("totalFeedbacks", totalFeedback.get());
            data.put("totalUsers", totalUser.get());
            data.put("totalProducts", totalProduct.get());
            data.put("top10ProductSelling", top10ProductSelling.get());

            // Chuyển dữ liệu mới và cũ thành JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String previousJson = objectMapper.writeValueAsString(previousData);
            String newJson = objectMapper.writeValueAsString(data);

            // Kiểm tra xem dữ liệu có thay đổi không
            if (!previousJson.equals(newJson)) {
                // Nếu có thay đổi, gửi qua WebSocket
                String jsonData = new ObjectMapper().writeValueAsString(data);
                session.sendMessage(new TextMessage(jsonData));

                // Cập nhật dữ liệu cũ
                previousData = new HashMap<>(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}