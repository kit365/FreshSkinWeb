package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/orders")
public class OrderConntroller {
    OrderService orderService;

    @PostMapping("/create_guest")
    public ResponseAPI<OrderResponse> createOrderByGuest(@RequestBody CreateOrderRequest createOrderRequest) {
        String message = "Create Order Success";
        var create = orderService.addNewUser(createOrderRequest);

        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }
    @PostMapping("/create_user")
    public ResponseAPI<OrderResponse> createOrderByUser(@RequestBody CreateOrderRequest createOrderRequest) {
        String message = "Create Order Success";
        var create = orderService.addOldUser(createOrderRequest);

        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }
}
