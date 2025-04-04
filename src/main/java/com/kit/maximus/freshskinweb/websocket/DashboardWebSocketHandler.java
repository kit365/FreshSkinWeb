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
import com.kit.maximus.freshskinweb.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class DashboardWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private DashboardService dashboardService;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private Map<String, Object> previousData = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("📩 Kết nối cổng Dashboard mới từ: {}", session.getId());
        sessions.add(session);
        sendDataDashBoard(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("❌ Kết nối đóng từ cổng dashboard: {}", session.getId());
        sessions.remove(session);
        previousData.clear();
    }

//    @Scheduled(fixedRate = 5000) // Gửi mỗi 5s
//    public void sendPeriodicDataToClients() {
//        sessions.removeIf(session -> !session.isOpen()); // Loại bỏ session đã đóng
//        for (WebSocketSession session : sessions) {
//            if (session.isOpen()) {
//                sendDataDashBoard(session);
//            }
//        }
//    }
//
//
//    public void sendDataDashBoard(WebSocketSession session) {
//        CompletableFuture<Map<String, Object>> futureData = fetchDataFromService();
//
//        futureData.thenAcceptAsync(data -> {
//            if (session == null || !session.isOpen()) {
//                log.warn("⚠️ Session đã đóng, không gửi được dữ liệu.");
//                return;
//            }
//
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                String previousJson = objectMapper.writeValueAsString(previousData);
//                String newJson = objectMapper.writeValueAsString(data);
//
//                if (!previousJson.equals(newJson)) {
//                // Nếu có thay đổi, gửi qua WebSocket
//                String jsonData = new ObjectMapper().writeValueAsString(data);
//                session.sendMessage(new TextMessage(jsonData));
//
//                // Cập nhật dữ liệu cũ
//                previousData = new HashMap<>(data);
//            }
//            } catch (Exception e) {
//                log.error("⚠️ Lỗi khi gửi dữ liệu WebSocket: ", e);
//            }
//        });
//    }


    //    @Scheduled(fixedRate = 5000) // Gửi mỗi 5s
//    public void sendPeriodicDataToClients() {
//        sessions.removeIf(session -> !session.isOpen()); // Loại bỏ session đã đóng
//        for (WebSocketSession session : sessions) {
//            if (session.isOpen()) {
//                executorService.submit(() -> sendDataDashBoard(session)); // Chạy hàm gửi dữ liệu trong thread riêng
//            }
//        }
//    }
//
//    public void sendDataDashBoard(WebSocketSession session) {
//        CompletableFuture<Map<String, Object>> futureData = fetchDataFromService();
//
//        futureData.thenAcceptAsync(data -> {
//            if (session == null || !session.isOpen()) {
//                log.warn("⚠️ Session đã đóng, không gửi được dữ liệu.");
//                return;
//            }
//
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                String previousJson = objectMapper.writeValueAsString(previousData);
//                String newJson = objectMapper.writeValueAsString(data);
//
//                if (!previousJson.equals(newJson)) {
//                    String jsonData = objectMapper.writeValueAsString(data);
//                    session.sendMessage(new TextMessage(jsonData));
//
//                    // Cập nhật dữ liệu cũ
//                    previousData = new HashMap<>(data);
//                }
//            } catch (Exception e) {
//                log.error("⚠️ Lỗi khi gửi dữ liệu WebSocket: ", e);
//            }
//        }, executorService); // Chạy `thenAcceptAsync` với thread pool riêng
//    }


    @Scheduled(fixedRate = 5000) // Gửi mỗi 5s
    public void sendPeriodicDataToClients() {
        sessions.removeIf(session -> !session.isOpen()); // Loại bỏ session đã đóng
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                futures.add(CompletableFuture.runAsync(() -> sendDataDashBoard(session), executorService));
            }
        }

        // Đảm bảo tất cả dữ liệu đã được gửi trước khi tiếp tục
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void sendDataDashBoard(WebSocketSession session) {
        CompletableFuture<Map<String, Object>> futureData = fetchDataFromService();

        futureData.thenAcceptAsync(data -> {
            if (session == null || !session.isOpen()) {
                log.warn("⚠️ Session đã đóng, không gửi được dữ liệu.");
                return;
            }

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String previousJson = objectMapper.writeValueAsString(previousData);
                String newJson = objectMapper.writeValueAsString(data);

                if (!previousJson.equals(newJson)) {
                    String jsonData = objectMapper.writeValueAsString(data);
                    session.sendMessage(new TextMessage(jsonData));

                    // Cập nhật dữ liệu cũ
                    previousData = new HashMap<>(data);
                }
            } catch (Exception e) {
                log.error("⚠️ Lỗi khi gửi dữ liệu WebSocket: ", e);
            }
        }, executorService); // Sử dụng thread pool riêng cho each task
    }


    //nhan data tu dashboardService
    private CompletableFuture<Map<String, Object>> fetchDataFromService() {
        return CompletableFuture.allOf(
                dashboardService.getTotalOrder(),
                dashboardService.getOrderCompleted(),
                dashboardService.getOrderPending(),
                dashboardService.getOrderCanceled(),
                dashboardService.getTotalRevenue(),
                dashboardService.getTotalBlogs(),
                dashboardService.getTotalReviews(),
                dashboardService.getTotalUsers(),
                dashboardService.getTotalProducts(),
                dashboardService.getTop10SellingProducts(),
                dashboardService.getTop5CategoryHaveTopProduct(),
                dashboardService.getRatingStatsByDate(),
                dashboardService.getRevenueByDate(),
                dashboardService.getRevenueByCategories(),
                dashboardService.getStatisticSkinTest(),
                dashboardService.getDiscountStatistics()
        ).thenApply(v -> { //co ket qua, bien doi cac data duoc thuc hien trong CompletableFuture thanh map
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("totalOrder", dashboardService.getTotalOrder().join());
                data.put("totalOrderCompleted", dashboardService.getOrderCompleted().join());
                data.put("totalOrderPending", dashboardService.getOrderPending().join());
                data.put("totalOrderCanceled", dashboardService.getOrderCanceled().join());
                data.put("totalRevenue", dashboardService.getTotalRevenue().join());
                data.put("totalBlogs", dashboardService.getTotalBlogs().join());
                data.put("totalFeedbacks", dashboardService.getTotalReviews().join());
                data.put("totalUsers", dashboardService.getTotalUsers().join());
                data.put("totalProducts", dashboardService.getTotalProducts().join());
                data.put("top10ProductSelling", dashboardService.getTop10SellingProducts().join());
                data.put("Top5CategoryHaveTopProduct", dashboardService.getTop5CategoryHaveTopProduct().join());
                data.put("ratingStartsByDate", dashboardService.getRatingStatsByDate().join());
                data.put("revenueByDate", dashboardService.getRevenueByDate().join());
                data.put("revenueByCategories", dashboardService.getRevenueByCategories().join());
                data.put("statisticSkinTest", dashboardService.getStatisticSkinTest().join());
                data.put("discountDetail", dashboardService.getDiscountStatistics().join());
                return data;
            } catch (Exception e) {
                log.error("⚠️ Lỗi khi lấy dữ liệu dashboard: ", e);
                return Collections.emptyMap();
            }
        });
    }
}
