package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/orders")
public class OrderConntroller {
    OrderService orderService;

    @PostMapping("/create")
    public ResponseAPI<OrderResponse> createOrderByGuest(@RequestBody CreateOrderRequest createOrderRequest) {
        String message = "Tạo đơn hàng thành công";
        var create = orderService.addOrder(createOrderRequest);

        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }
//    @PostMapping("/create_user")
//    public ResponseAPI<OrderResponse> createOrderByUser(@RequestBody CreateOrderRequest createOrderRequest) {
//        String message = "Create Order Success";
//        var create = orderService.addOldUser(createOrderRequest);
//
//        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
//    }
//    @GetMapping("/search/{orderId}")
//    public ResponseAPI<OrderResponse> searchOrderByOrderId(@PathVariable long orderId) {
//        String message = "Search Order Success";
//        var order = orderService.getOrderById(orderId);
//        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
//    }
    @GetMapping("/show")
    public ResponseAPI<List<OrderResponse>> getAllOrder() {
        String message = "Hiện tất cả các đơn hàng thành công";
        List<OrderResponse> order = orderService.getAllOrder();
        return ResponseAPI.<List<OrderResponse>>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
    }

//    @PatchMapping("/update/{orderId}")
//    public ResponseAPI<OrderResponse> updateOrder(@Valid @PathVariable Long orderId, @RequestBody UpdateOrderRequest updateOrderRequest) {
//        String message = "Update Order Success";
//        var create = orderService.updateOrder(orderId, updateOrderRequest);
//
//        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
//    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseAPI<OrderResponse> deleteOrderByOrderId(@PathVariable Long orderId) {
        String message = "Xóa đơn hàng thành công";
        orderService.deleteOrder(orderId);
        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @DeleteMapping("/deleted/{orderId}")
    public ResponseAPI<OrderResponse> deleteByOrderId(@PathVariable Long orderId) {
        String message = "Xóa đơn hàng thành công";
        var order = orderService.deleted(orderId);
        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
    }


}
