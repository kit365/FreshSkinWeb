package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderIdResponse;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.OrderService;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/orders")
@Slf4j
public class OrderController {
    OrderService orderService;

    @PostMapping("/create")
    public ResponseAPI<OrderIdResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String message = "Tạo đơn hàng thành công";
        var create = orderService.addOrder(orderRequest);

        return ResponseAPI.<OrderIdResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }

//    @GetMapping("/show")
//    public ResponseAPI<List<OrderResponse>> getAllOrder() {
//        String message = "Hiện tất cả các đơn hàng thành công";
//        List<OrderResponse> order = orderService.getAllOrder();
//        return ResponseAPI.<List<OrderResponse>>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
//    }



    @GetMapping
    public ResponseAPI<Map<String, Object>> getAllOrder(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var result = orderService.getAllOrders(status, keyword, orderId, page, size);
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }


    @GetMapping("/{id}")
    public ResponseAPI<OrderResponse> getOrderById(@PathVariable String id) {
        String message = "Tạo đơn hàng thành công";
        var order = orderService.getOrderById(id);

        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateOrder(@RequestBody Map<String, Object> request) {
        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<String> ids = (List<String>) request.get("id");
        String status = request.get("orderStatus").toString(); // Đổi từ 'status' thành 'orderStatus'

        var result = orderService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("edit/{orderId}")
    public ResponseAPI<OrderResponse> updateOrder( @PathVariable String orderId, @RequestBody OrderRequest orderStatus) {
        var create = orderService.update(orderId, orderStatus);

        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(create).build();
    }


    //    @PatchMapping("/update/{orderId}")
//    public ResponseAPI<OrderResponse> updateOrder(@Valid @PathVariable Long orderId, @RequestBody UpdateOrderRequest updateOrderRequest) {
//        String message = "Update Order Success";
//        var create = orderService.updateOrder(orderId, updateOrderRequest);
//
//        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
//    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseAPI<OrderResponse> deleteOrderByOrderId(@PathVariable String orderId) {
        String message = "Xóa đơn hàng thành công";
        orderService.deleteOrder(orderId);
        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @DeleteMapping("/deleted/{orderId}")
    public ResponseAPI<OrderResponse> deleteByOrderId(@PathVariable String orderId) {
        String message = "Xóa đơn hàng thành công";
        var order = orderService.deleted(orderId);
        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
    }


}
