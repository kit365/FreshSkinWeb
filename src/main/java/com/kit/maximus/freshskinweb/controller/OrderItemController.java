package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.orderItem.CreateOrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderItemResponse;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.OrderItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/orderitem")
public class OrderItemController {
    OrderItemService orderItemService;

    @PostMapping("/create")
    public ResponseAPI<OrderItemResponse> createOrderItem (@RequestBody CreateOrderItemRequest request) {
        String message = "Đơn hàng được thêm vào giỏ ";
        orderItemService.add(request);
        return ResponseAPI.<OrderItemResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<OrderItemResponse>> showAll(){
        List<OrderItemResponse> showList= orderItemService.showAll();
        return ResponseAPI.<List<OrderItemResponse> >builder()
                .code(HttpStatus.OK.value())
                .data(showList)
                .build();
    }

}
