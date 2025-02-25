package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderItemResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.OrderItemService;
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
@RequestMapping("/admin/orderitem")
public class OrderItemController {
    OrderItemService orderItemService;

    @PostMapping("/create")
    public ResponseAPI<OrderItemResponse> createOrderItem (@Valid @RequestBody OrderItemRequest request) {
        String message = "Sản phẩm trong đơn hàng được thêm vào giỏ ";
        orderItemService.add(request);
        return ResponseAPI.<OrderItemResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<OrderItemResponse>> showAll(){
        String message = "Hiển thị danh sách sản phẩm trong đơn hàng thành công";
        List<OrderItemResponse> showList= orderItemService.showAll();
        return ResponseAPI.<List<OrderItemResponse> >builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(showList)
                .build();
    }

    @PatchMapping("/update/{id}")
    public ResponseAPI<OrderItemResponse> update(@Valid @PathVariable Long id, @Valid @RequestBody OrderItemRequest request) {
        String message = "Thay đổi sản phẩm trong đơn hàng thành công!";
        orderItemService.update(id, request);
        return ResponseAPI.<OrderItemResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseAPI<OrderItemResponse> delete(@PathVariable Long id) {
        String message = "Xóa Sản phẩm trong đơn hàng thành công!";
        orderItemService.delete(id);
        return ResponseAPI.<OrderItemResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

}
