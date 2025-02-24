package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.orderItem.CreateOrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderItemResponse;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.OrderItemEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderItemMapper;
import com.kit.maximus.freshskinweb.repository.OrderItemRepository;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderItemService {
    OrderItemRepository repository;
    OrderItemMapper mapper;
    OrderRepository orderRepository;

    public boolean add(CreateOrderItemRequest request) {
        OrderItemEntity orderItemEntity = mapper.toOrderItemEntity(request);
        OrderEntity orderEntity = null;
        if(request.getOrder() != null) {
            orderEntity = orderRepository.findById(request.getOrder()).orElseThrow(()-> new AppException(ErrorCode.BLOG_ITEM_NOT_FOUND));
            orderItemEntity.setOrder(orderEntity);
        } else {
            orderItemEntity.setOrder(null);
        }
        repository.save(orderItemEntity);
        return true;
    }

    public List<OrderItemResponse> showAll(){
        return repository.findAll().stream().map(mapper::toOrderItemResponse).collect(Collectors.toList());
    }

}
