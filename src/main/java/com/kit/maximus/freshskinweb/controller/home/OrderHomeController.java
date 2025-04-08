package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderIdResponse;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.order.OrderService;
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

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/orders")
public class OrderHomeController {

    OrderService orderService;


    @PostMapping("/create")
    public ResponseAPI<OrderIdResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String message = "Tạo đơn hàng thành công";
        var create = orderService.addOrder(orderRequest);

        return ResponseAPI.<OrderIdResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }


    //SHOW LIST ORDER THEO USER
    @GetMapping("/user/{userId}")
    public ResponseAPI<List<OrderResponse>> getUserOrders(
            @PathVariable Long userId) {
        List<OrderResponse> result = orderService.getUserOrders(userId);
        return ResponseAPI.<List<OrderResponse>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    // TRA CỨU ĐƠN HÀNG

    //Tra cứu theo mã order
    @GetMapping("/{orderId}")
    public ResponseAPI<OrderResponse> trackOrder(@PathVariable String orderId) {
        var order = orderService.getOrderById(orderId);
        return ResponseAPI.<OrderResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Tra cứu đơn hàng thành công, đơn hàng: " + orderId)
                .data(order)
                .build();
    }


    //Tra cứu order theo sđt
    @GetMapping()
    public ResponseAPI<List<OrderResponse>> trackOrderByPhoneOrEmailOrBoth(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email)
    {
        List<OrderResponse> order = orderService.getOrdersByEmailAndPhoneNumber(email, phone);

        if(order == null) {
            return ResponseAPI.<List<OrderResponse>>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Không tìm thấy đơn hàng")
                    .data(order)
                    .build();
        }

        return ResponseAPI.<List<OrderResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Tra cứu đơn hàng thành công")
                .data(order)
                .build();
    }

}