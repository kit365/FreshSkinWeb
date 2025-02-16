package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.order.UpdateOrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import jakarta.persistence.criteria.Order;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    UserRepository userRepository;
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    UserService userService;

    public OrderResponse addOrder(CreateOrderRequest createOrderRequest) {
        var order = orderMapper.toOrderEntity(createOrderRequest);
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }


//    public OrderResponse addOldUser(CreateOrderRequest createOrderRequest) {
//        var user = userService.getUser(createOrderRequest.getUsername());
//        var order = orderMapper.toOrderEntity(createOrderRequest, user);
//
//        return orderMapper.toOrderResponse(orderRepository.save(order));
//    }

//    public OrderResponse getOrderById(Long orderId) {
//        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
//
//        return orderMapper.toOrderResponse(order);
//    }

    public List<OrderResponse> getAllOrder() {
        List<OrderEntity> orders = orderRepository.findAll();
        return orderMapper.toOrderResponseList(orders);
    }

    public void deleteOrder(Long orderId) {
        if(!orderRepository.existsById(orderId)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(orderId);
    }



//    public OrderResponse updateOrder(Long orderId, UpdateOrderRequest updateOrderRequest) {
//        // Lấy OrderEntity từ database (không phải OrderResponse)
//        OrderEntity orderEntity = orderRepository.findById(orderId)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
//
//        // Ánh xạ thông tin update từ request vào entity
//        orderMapper.updateOrder(orderEntity, updateOrderRequest);
//
//        // Lưu lại vào database
//        OrderEntity updatedOrder = orderRepository.save(orderEntity);
//
//        // Trả về OrderResponse (DTO)
//        return orderMapper.toOrderResponse(updatedOrder);
//    }


}
