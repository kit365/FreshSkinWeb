package com.kit.maximus.freshskinweb.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    OrderService orderService;

    public String getTotalRevenue() {
        return orderService.countRevenue();
    }

    public long getOrderCompleted() {
        return orderService.countCompleted();
    }

    public long getOrderCanceled() {
        return orderService.countCanceled();
    }

    public long getOrderPending() {
        return orderService.countPending();
    }

    public long getTotalOrder() {
        return orderService.countTotalOrders();
    }




}
