package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.OrderItemEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.ProductVariantRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    UserRepository userRepository;
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    UserService userService;
    ProductVariantRepository productVariantRepository;
    ProductMapper productMapper;
    ProductRepository productRepository;

    @Transactional
    public OrderIdResponse addOrder(OrderRequest orderRequest) {
        System.out.println(orderRequest);
        OrderEntity order = orderMapper.toOrderEntity(orderRequest);

        //Kêu Dũng có gửi UserID không => băắt Dũng phải gửi thêm id User nếu có sẵn
        if (orderRequest.getUserId() != null) {
            var user = userRepository.findById(orderRequest.getUserId()).orElse(null);
            order.setUser(user);
        } else {
            order.setUser(null);
        }

        //No da gui Product variant ID va quantity
        // xai OrderitemRepo de tim ID roi set vao, set 2 chiều để tạo OrderItem trong Order
        // Set object ProductVariant và số lượng
        if (orderRequest.getOrderItems() != null) {

            for (OrderItemRequest orderItem : orderRequest.getOrderItems()) {
                System.out.println("in loop" + orderItem);

                if (orderItem != null) {

                    //Đóng vai trò làm phễu để lưu tạm thời, sau đó bỏ vào trong List OrderItems trong Order
                    //Mỗi lần lặp là 1 đối tượng mới để lưu vào trong list OrderItems của Order
                    OrderItemEntity orderItemEntity = new OrderItemEntity();

                    ProductVariantEntity productVariantEntity = productVariantRepository.findById(orderItem.getProductVariantId()).orElse(null);

                    if (productVariantEntity != null) {
                        orderItemEntity.setProductVariant(productVariantEntity);

                        System.out.println("in conditions" + orderItem);
                    } else {
                        orderItemEntity.setProductVariant(null);
                    }

                    orderItemEntity.setQuantity(orderItem.getQuantity());
                    Double subtotal = productVariantEntity.getPrice() * orderItem.getQuantity();
                    orderItemEntity.setSubtotal(subtotal);
                    order.addOrderItem(orderItemEntity);
                }
            }
        }

        return orderMapper.toOrderResponseCreate(orderRepository.save(order));
    }


    public OrderResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return orderMapper.toOrderResponse(order);
    }

    public ProductResponseDTO getProductByVariant(Long id) {
        ProductVariantEntity varirant = productVariantRepository.findById(id).orElse(null);
        ProductEntity product = varirant.getProduct();
        return productMapper.productToProductResponseDTO(product);
    }

    public List<OrderResponse> getAllOrder() {
        List<OrderEntity> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();

//        if (orders != null && !orders.isEmpty()) {
//            for (OrderEntity orderEntity : orders) {
//                OrderResponse orderResponse = orderMapper.toOrderResponse(orderEntity);
//
//                List<OrderItemResponse> orderItemsResponse = new ArrayList<>();
//
//                if (orderResponse.getOrderItems() != null) {
//                    for (OrderItemResponse orderItemResponse : orderResponse.getOrderItems()) {
//                        ProductVariantResponse productVariantResponse = orderItemResponse.getProductVariant();
//
//                        if (productVariantResponse != null) {
//                            ProductResponseDTO productResponse = getProductByVariant(productVariantResponse.getId());
//                            productVariantResponse.setProduct(productResponse);
//                        }
//
//                        OrderItemResponse updatedOrderItemResponse = OrderItemResponse.builder()
//                                .orderItemId(orderItemResponse.getOrderItemId())
//                                .order(orderItemResponse.getOrder())
//                                .productVariant(productVariantResponse)
//                                .quantity(orderItemResponse.getQuantity())
//                                .subtotal(orderItemResponse.getSubtotal())
//                                .status(orderItemResponse.getStatus())
//                                .build();
//
//                        orderItemsResponse.add(updatedOrderItemResponse);
//                    }
//                }

//                orderResponse.setOrderItems(orderItemsResponse);
//                orderResponses.add(orderResponse);
//            }
//        }

        return null;
    }



    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(orderId);
    }

    public OrderResponse deleted(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        orderEntity.setDeleted(true);
        OrderEntity result = orderRepository.save(orderEntity);

        return orderMapper.toOrderResponse(result);
    }

}
