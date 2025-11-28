//package com.kit.maximus.freshskinweb.presentation.websocket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kit.maximus.freshskinweb.business.service.DashboardService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.*;
//import java.util.concurrent.*;
//
//@Slf4j
//@Component
//public class DashboardWebSocketHandler extends TextWebSocketHandler {
//
//    @Autowired
//    private DashboardService dashboardService;
//
//    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
//    private Map<String, Object> previousData = new ConcurrentHashMap<>();
//    private final ExecutorService executorService = Executors.newCachedThreadPool();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        log.info("📩 Kết nối cổng Dashboard mới từ: {}", session.getId());
//        sessions.add(session);
//        sendDataDashBoard(session);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        log.info("❌ Kết nối đóng từ cổng dashboard: {}", session.getId());
//        sessions.remove(session);
//        previousData.clear();
//    }
//
//
//    @Scheduled(fixedRate = 5000) // Gửi mỗi 5s
//    public void sendPeriodicDataToClients() {
//        sessions.removeIf(session -> !session.isOpen()); // Loại bỏ session đã đóng
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//
//        for (WebSocketSession session : sessions) {
//            if (session.isOpen()) {
//                futures.add(CompletableFuture.runAsync(() -> sendDataDashBoard(session), executorService));
//            }
//        }
//
//        // Đảm bảo tất cả dữ liệu đã được gửi trước khi tiếp tục
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
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
//        }, executorService); // Sử dụng thread pool riêng cho each task
//    }
//
//
//    //nhan data tu dashboardService
//    private CompletableFuture<Map<String, Object>> fetchDataFromService() {
//        return CompletableFuture.allOf(
//                dashboardService.getTotalOrder(),
//                dashboardService.getOrderCompleted(),
//                dashboardService.getOrderPending(),
//                dashboardService.getOrderCanceled(),
//                dashboardService.getCountDriving(),
//                dashboardService.getTotalRevenue(),
//                dashboardService.getTotalBlogs(),
//                dashboardService.getTotalReviews(),
//                dashboardService.getTotalUsers(),
//                dashboardService.getTotalProducts(),
//                dashboardService.getTop10SellingProducts(),
//                dashboardService.getTop5CategoryHaveTopProduct(),
//                dashboardService.getRatingStatsByDate(),
//                dashboardService.getRevenueByDate(),
//                dashboardService.getRevenueByCategories(),
//                dashboardService.getStatisticSkinTest(),
//                dashboardService.getDiscountStatistics()
//        ).thenApply(v -> { //co ket qua, bien doi cac data duoc thuc hien trong CompletableFuture thanh map
//            try {
//                Map<String, Object> data = new HashMap<>();
//                data.put("totalOrder", dashboardService.getTotalOrder().join());
//                data.put("totalOrderCompleted", dashboardService.getOrderCompleted().join());
//                data.put("totalOrderPending", dashboardService.getOrderPending().join());
//                data.put("totalOrderCanceled", dashboardService.getOrderCanceled().join());
//                data.put("totalOrderShipping", dashboardService.getCountDriving().join());
//                data.put("totalRevenue", dashboardService.getTotalRevenue().join());
//                data.put("totalBlogs", dashboardService.getTotalBlogs().join());
//                data.put("totalFeedbacks", dashboardService.getTotalReviews().join());
//                data.put("totalUsers", dashboardService.getTotalUsers().join());
//                data.put("totalProducts", dashboardService.getTotalProducts().join());
//                data.put("top10ProductSelling", dashboardService.getTop10SellingProducts().join());
//                data.put("Top5CategoryHaveTopProduct", dashboardService.getTop5CategoryHaveTopProduct().join());
//                data.put("ratingStartsByDate", dashboardService.getRatingStatsByDate().join());
//                data.put("revenueByDate", dashboardService.getRevenueByDate().join());
//                data.put("revenueByCategories", dashboardService.getRevenueByCategories().join());
//                data.put("statisticSkinTest", dashboardService.getStatisticSkinTest().join());
//                data.put("discountDetail", dashboardService.getDiscountStatistics().join());
//                return data;
//            } catch (Exception e) {
//                log.error("⚠️ Lỗi khi lấy dữ liệu dashboard: ", e);
//                return Collections.emptyMap();
//            }
//        });
//    }
//}
