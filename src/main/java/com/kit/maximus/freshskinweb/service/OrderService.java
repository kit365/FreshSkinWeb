package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
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

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    UserRepository userRepository;
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    UserService userService;

    public OrderResponse addNewUser(CreateOrderRequest createOrderRequest) {
        var order = orderMapper.toOrderEntity(createOrderRequest);
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }


    public OrderResponse addOldUser(CreateOrderRequest createOrderRequest) {
        var user = userService.getUser(createOrderRequest.getUsername());

//        var order = orderMapper.toOrderEntity(createOrderRequest);
//        order.setUser(user);
//        order.setUsername(user.getUsername());
//        order.setFirstName(user.getFirstName());
//        order.setLastName(user.getLastName());
//        order.setEmail(user.getEmail());
//        order.setPhoneNumber(user.getPhone());
//        order.setAddress(user.getAddress());
        var order = orderMapper.toOrderEntity(createOrderRequest, user);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

}
