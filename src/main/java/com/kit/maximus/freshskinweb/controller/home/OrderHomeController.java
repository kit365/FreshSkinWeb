package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderIdResponse;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.OrderService;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.ProductService;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/order")
public class OrderHomeController {

    OrderService orderService;


    @PostMapping("/create")
    public ResponseAPI<OrderIdResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String message = "Tạo đơn hàng thành công";
        var create = orderService.addOrder(orderRequest);

        return ResponseAPI.<OrderIdResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }

//    @GetMapping()
//    public ResponseAPI<List<OrderResponse>> getAllOrder() {
//        String message = "Hiện tất cả các đơn hàng thành công";
//        List<OrderResponse> order = orderService.getAllOrder();
//        return ResponseAPI.<List<OrderResponse>>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
//    }

//    @GetMapping
//    public ResponseAPI<List<OrderResponse>> getAllOrder(
//            @RequestParam(required = false) OrderStatus status,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String orderId
//
//    ) {
//        var result = orderService.getAllOrder(status, keyword, orderId);
//        return ResponseAPI.<List<OrderResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
//    }


    //SHOW LIST ORDER THEO USER
    @GetMapping("/user/{userId}")
    public ResponseAPI<Map<String, Object>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) OrderStatus priorityStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var result = orderService.getUserOrders(userId, status, keyword, orderId, page, size, sortBy, priorityStatus);
        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    // TRA CỨU ĐƠN HÀNG
    @GetMapping("/{orderId}")
    public ResponseAPI<OrderResponse> trackOrder(@PathVariable String orderId) {
        var order = orderService.getOrderById(orderId);
        return ResponseAPI.<OrderResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Tra cứu đơn hàng thành công, đơn hàng: " + orderId)
                .data(order)
                .build();
    }
}