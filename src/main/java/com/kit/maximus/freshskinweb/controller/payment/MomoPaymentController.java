//package com.kit.maximus.freshskinweb.controller.payment;
//
//import com.kit.maximus.freshskinweb.constant.MomoParameter;
//import com.kit.maximus.freshskinweb.dto.response.CreateMomoResponse;
//import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
//import com.kit.maximus.freshskinweb.entity.OrderEntity;
//import com.kit.maximus.freshskinweb.repository.OrderRepository;
//import com.kit.maximus.freshskinweb.service.MomoService;
//import com.kit.maximus.freshskinweb.utils.OrderStatus;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
////@CrossOrigin(origins = "*")
//@RequiredArgsConstructor
//@RestController
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@RequestMapping("/api/momo")
//public class MomoPaymentController {
//
//
//    OrderRepository orderRepository;
//
//    MomoService momoService;
//
//    @PostMapping("create")
//    public ResponseAPI<CreateMomoResponse> createQR() {
//        String message = "Tạo mã QR thành công";
//        CreateMomoResponse response = momoService.createQR();
//        return ResponseAPI.<CreateMomoResponse>builder().code(HttpStatus.OK.value()).message(message).data(response).build();
//    }
//
////    @GetMapping("/ipn-handler")
////    public String ipnHandler(@RequestParam Map<String, String> params) {
////        String resultCodeStr = params.get(MomoParameter.RESULT_CODE);
////
////        Integer resultCode = Integer.valueOf(resultCodeStr);
////        return resultCode == 0 ? "Giao dịch thành công" : "Giao dịch thất bại";
////    }
//@GetMapping("/ipn-handler")
//public ResponseAPI<String> ipnHandler(@RequestParam Map<String, String> params) {
//    String orderIdStr = params.get("orderId");
//    String resultCodeStr = params.get("resultCode");
//    String message = "Cập nhập đơn hàng thành công";
//
//    if (orderIdStr == null || resultCodeStr == null) {
//        return ResponseAPI.<String>builder().code(HttpStatus.BAD_REQUEST.value()).message("Thiếu tham số").build();
//    }
//
//    String orderId = orderIdStr;
//    Integer resultCode = Integer.valueOf(resultCodeStr);
//
//    OrderEntity order = orderRepository.findById(orderId)
//            .orElseThrow(() -> new RuntimeException("Order không tồn tại"));
//
//    if (resultCode == 0) {
//        order.setOrderStatus(OrderStatus.COMPLETED); // Đánh dấu đã thanh toán
//    } else {
//        order.setOrderStatus(OrderStatus.CANCELED);
//    }
//
//    orderRepository.save(order);
//    return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message).build();
//}
//
//
//
//}
//
