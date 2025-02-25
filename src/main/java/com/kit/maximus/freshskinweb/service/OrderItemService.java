package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.orderItem.CreateOrderItemRequest;
import com.kit.maximus.freshskinweb.dto.request.orderItem.UpdateOrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.OrderItemResponse;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.OrderItemEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderItemMapper;
import com.kit.maximus.freshskinweb.repository.OrderItemRepository;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.ProductVariantRepository;
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
    OrderItemRepository orderItemRepository;
    OrderItemMapper orderItemMapper;
    OrderRepository orderRepository;
    ProductVariantRepository productVariantRepository;

//    public boolean add(CreateOrderItemRequest request) {
//        OrderItemEntity orderItemEntity = orderItemMapper.toOrderItemEntity(request);
//        OrderEntity orderEntity = null;
//        if(request.getOrderId() != null) {
//            orderEntity = orderRepository.findById(request.getOrderId()).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_FOUND));
//            orderItemEntity.setOrder(orderEntity);
//        } else {
//            orderItemEntity.setOrder(null);
//        }
//        ProductVariantEntity productVariant = productVariantRepository.findById(request.getProductVariantId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
//        if(productVariant == null) {
//            orderItemEntity.setProductVariant(null);
//        } else {
//            orderItemEntity.setProductVariant(productVariant);
//        }
//        orderItemRepository.save(orderItemEntity);
//        return true;
//    }

    public boolean add(CreateOrderItemRequest request) {
        OrderItemEntity orderItemEntity = orderItemMapper.toOrderItemEntity(request);
        OrderEntity orderEntity = null;
        if(request.getOrderId() != null) {
            orderEntity = orderRepository.findById(request.getOrderId()).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_FOUND));
            orderItemEntity.setOrder(orderEntity);
        } else {
            orderItemEntity.setOrder(null);
        }
        ProductVariantEntity productVariant = null;
        if(request.getProductVariantId() != null) {
            productVariant = productVariantRepository.findById(request.getProductVariantId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
            orderItemEntity.setProductVariant(productVariant);
        } else {
            orderItemEntity.setOrder(null);
        }
        orderItemRepository.save(orderItemEntity);
        return true;
    }

    public List<OrderItemResponse> showAll(){
        return orderItemRepository.findAll().stream().map(orderItemMapper::toOrderItemResponse).toList();
    }

    public boolean update(Long id, UpdateOrderItemRequest request) {
        var orderItem = orderItemRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND));
        orderItemMapper.updateOrderItems(orderItem, request);
        orderItemRepository.save(orderItem);
        return true;
    }

    public void delete(Long id) {
        orderItemRepository.deleteById(id);
    }



}
