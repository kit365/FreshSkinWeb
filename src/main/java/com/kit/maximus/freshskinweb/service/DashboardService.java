package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.service.blog.BlogService;
import com.kit.maximus.freshskinweb.service.order.OrderService;
import com.kit.maximus.freshskinweb.service.users.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    OrderService orderService;
    BlogService blogService;
    ReviewService reviewService;
    UserService userService;
    ProductService productService;

    @Async
    public CompletableFuture<String> getTotalRevenue() {
        return CompletableFuture.completedFuture(orderService.countRevenue());
    }

    @Async
    public CompletableFuture<Long> getOrderCompleted() {
        return CompletableFuture.completedFuture(orderService.countCompleted());
    }

    @Async
    public CompletableFuture<Long> getOrderCanceled() {
        return CompletableFuture.completedFuture(orderService.countCanceled());
    }

    @Async
    public CompletableFuture<Long> getOrderPending() {
        return CompletableFuture.completedFuture(orderService.countPending());
    }

    @Async
    public CompletableFuture<Long> getTotalOrder() {
        return CompletableFuture.completedFuture(orderService.countTotalOrders());
    }

    @Async
    public CompletableFuture<Long> getTotalBlogs() {
        return CompletableFuture.completedFuture(blogService.countBlogs());
    }

    @Async
    public CompletableFuture<Long> getTotalReviews() {
        return CompletableFuture.completedFuture(reviewService.countReview());
    }

    @Async
    public CompletableFuture<Long> getTotalUsers() {
        return CompletableFuture.completedFuture(userService.countUser());
    }

    @Async
    public CompletableFuture<Long> getTotalProducts() {
        return CompletableFuture.completedFuture(productService.countProduct());
    }


    @Async
    public  CompletableFuture<List<ProductResponseDTO>> getTop10SellingProducts() {
        return CompletableFuture.completedFuture(productService.top10SellingProducts());
    }


}
