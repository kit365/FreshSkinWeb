package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.OrderService;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/order")
public class OrderHomeController {

    OrderService orderService;


    @PostMapping("/create")
    public ResponseAPI<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String message = "Tạo đơn hàng thành công";
        var create = orderService.addOrder(orderRequest);

        return ResponseAPI.<OrderResponse>builder().code(HttpStatus.OK.value()).message(message).data(create).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<OrderResponse>> getAllOrder() {
        String message = "Hiện tất cả các đơn hàng thành công";
        List<OrderResponse> order = orderService.getAllOrder();
        return ResponseAPI.<List<OrderResponse>>builder().code(HttpStatus.OK.value()).message(message).data(order).build();
    }



}
