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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        OrderEntity order = orderMapper.toOrderEntity(orderRequest);

        order.setOrderId(generateOrderCode());

        //Kêu Dũng có gửi UserID không => băắt Dũng phải gửi thêm id User nếu có sẵn
        if (orderRequest.getUserId() != null) {
            var user = userRepository.findById(orderRequest.getUserId()).orElse(null);
            order.setUser(user);
        } else {
            order.setUser(null);
        }

        //Tạo order item thông qua order
        // Dũng chỉ gửi ProductVariantID và số lượng để tạo Order Items
        // xai OrderitemRepo de tim ID roi set vao, set 2 chiều để tạo OrderItem trong Order
        if (orderRequest.getOrderItems() != null) {

            for (OrderItemRequest orderItem : orderRequest.getOrderItems()) {

                if (orderItem != null) {

                    //Đóng vai trò làm phễu để lưu tạm thời, sau đó bỏ vào trong List OrderItems trong Order
                    //Mỗi lần lặp là 1 đối tượng mới để lưu vào trong list OrderItems của Order
                    OrderItemEntity orderItemEntity = new OrderItemEntity();

                    ProductVariantEntity productVariantEntity = productVariantRepository.findById(orderItem.getProductVariantId()).orElse(null);

                    if (productVariantEntity != null) {
                        orderItemEntity.setProductVariant(productVariantEntity);
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

    // Tạo ID cho order
    private String generateOrderCode() {
        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();

        // Lấy 2 số cuối của năm
        String year = String.valueOf(today.getYear()).substring(2);

        // Lấy tháng và ngày (định dạng 4 số, ví dụ 0226)
        String monthDay = today.format(DateTimeFormatter.ofPattern("MMdd"));

        // Sinh ngẫu nhiên 5 số
        String randomDigits = String.format("%05d", new Random().nextInt(100000));

        // Tạo mã đơn hàng hoàn chỉnh
        return  year + monthDay + randomDigits;
    }


    public OrderResponse getOrderById(String orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);

        //Dựa vào ProductVariantID, show ra thêm các field phụ như ảnh, title, slug của Product (Mặc dù product và Order không đươc liên kết nhau )
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemResponse> orderItemResponses = new ArrayList<>();
            
            for (OrderItemEntity orderItemEntity : order.getOrderItems()) {
                OrderItemResponse orderItemResponse = new OrderItemResponse();
                orderItemResponse.setOrderItemId(orderItemEntity.getOrderItemId());
                orderItemResponse.setQuantity(orderItemEntity.getQuantity());
                orderItemResponse.setSubtotal(orderItemEntity.getSubtotal());

                if (orderItemEntity.getProductVariant() != null) {
                    ProductVariantResponse productVariantResponse = new ProductVariantResponse();
                    productVariantResponse.setId(orderItemEntity.getProductVariant().getId());
                    productVariantResponse.setPrice(orderItemEntity.getProductVariant().getPrice());
                    productVariantResponse.setUnit(orderItemEntity.getProductVariant().getUnit());
                    productVariantResponse.setVolume(orderItemEntity.getProductVariant().getVolume());

                    if (orderItemEntity.getProductVariant().getProduct() != null) {
                        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
                        productResponseDTO.setTitle(orderItemEntity.getProductVariant().getProduct().getTitle());
                        productResponseDTO.setThumbnail(orderItemEntity.getProductVariant().getProduct().getThumbnail());
                        productResponseDTO.setDiscountPercent(orderItemEntity.getProductVariant().getProduct().getDiscountPercent());
                        productResponseDTO.setSlug(orderItemEntity.getProductVariant().getProduct().getSlug());
                        productResponseDTO.setId(orderItemEntity.getProductVariant().getProduct().getId());
                        productVariantResponse.setProduct(productResponseDTO);
                    }

                    orderItemResponse.setProductVariant(productVariantResponse);
                }

                orderItemResponses.add(orderItemResponse);
            }

            orderResponse.setOrderItems(orderItemResponses);
        }

        return orderResponse;
    }


//    public ProductResponseDTO getProductByVariant(Long id) {
//        ProductVariantEntity varirant = productVariantRepository.findById(id).orElse(null);
//        ProductEntity product = varirant.getProduct();
//        return productMapper.productToProductResponseDTO(product);
//    }

    public List<OrderResponse> getAllOrder() {
        List<OrderEntity> orders = orderRepository.findAll();

        List<OrderResponse> orderResponses = orderMapper.toOrderResponseList(orders);



        for (OrderResponse orderResponse : orderResponses) {

            orders.forEach(orderEntity -> {
                if(orderEntity.getOrderItems() != null) {
                    List<OrderItemResponse>  orderItemResponses = new ArrayList<>();
                    orderEntity.getOrderItems().forEach(orderItemEntity -> {
                        OrderItemResponse orderItemResponse = new OrderItemResponse();
                        orderItemResponse.setOrderItemId(orderItemEntity.getOrderItemId());
                        orderItemResponse.setQuantity(orderItemEntity.getQuantity());
                        orderItemResponse.setSubtotal(orderItemEntity.getSubtotal());

                        if(orderItemEntity.getProductVariant() != null) {
                            ProductVariantResponse productVariantResponse = new ProductVariantResponse();
                            productVariantResponse.setId(orderItemEntity.getProductVariant().getId());
                            productVariantResponse.setPrice(orderItemEntity.getProductVariant().getPrice());
                            productVariantResponse.setUnit(orderItemEntity.getProductVariant().getUnit());
                            productVariantResponse.setVolume(orderItemEntity.getProductVariant().getVolume());

                            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
                            productResponseDTO.setTitle(orderItemEntity.getProductVariant().getProduct().getTitle());
                            productResponseDTO.setThumbnail(orderItemEntity.getProductVariant().getProduct().getThumbnail());
                            productResponseDTO.setDiscountPercent(orderItemEntity.getProductVariant().getProduct().getDiscountPercent());
                            productResponseDTO.setSlug(orderItemEntity.getProductVariant().getProduct().getSlug());
                            productResponseDTO.setId(orderItemEntity.getProductVariant().getProduct().getId());
                            productVariantResponse.setProduct(productResponseDTO);
                            orderItemResponse.setProductVariant(productVariantResponse);
                            orderItemResponses.add(orderItemResponse);
                        }
                        orderResponse.setOrderItems(orderItemResponses);
                    });
                }
            });


        }




        //Truy xuat Product thông qua ProductVariantID


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

        return orderResponses;
    }


    public void deleteOrder(String orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(orderId);
    }

    public OrderResponse deleted(String orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        orderEntity.setDeleted(true);
        OrderEntity result = orderRepository.save(orderEntity);

        return orderMapper.toOrderResponse(result);
    }

}
