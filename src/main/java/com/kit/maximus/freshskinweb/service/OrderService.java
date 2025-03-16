package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderItemMapper;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.ProductVariantRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.specification.OrderSpecification;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    UserRepository userRepository;
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductVariantRepository productVariantRepository;
    EmailService emailService;
    OrderItemMapper orderItemMapper;

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
//                    Double subtotal = productVariantEntity.getPrice() * orderItem.getQuantity();
//                    orderItemEntity.setSubtotal(subtotal);
//                    orderItemEntity.calculateSubtotal();
                    assert productVariantEntity != null;
                    double discountPercent = (productVariantEntity.getProduct().getDiscount() != null
                            && productVariantEntity.getProduct().getDiscount().getDiscountPercentage() != 0.0)
                            ? productVariantEntity.getProduct().getDiscount().getDiscountPercentage() / 100.0
                            : 0.0;
                    orderItemEntity.setDiscountPrice(orderItemEntity.getSubtotal() * (1 - discountPercent));
                    order.addOrderItem(orderItemEntity);
                }
            }
        }
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getDiscountPrice() != 0 ? item.getDiscountPrice() : item.getSubtotal())
                .sum();
        order.setTotalPrice(totalPrice);

        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseCreate(savedOrder);

    }

    public void processOrder(String orderId) {
        emailService.sendOrderConfirmationEmail(orderId); // Gọi từ bên ngoài
    }


    public void saveOrder(OrderEntity order) {
        orderRepository.save(order);
    }

        public OrderStatus getOrderStatus (String orderStatus){
            try {
                return OrderStatus.valueOf((orderStatus.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status provided: '{}'", orderStatus);
                throw new AppException(ErrorCode.ORDER_STATUS_INVALID);
            }
        }

        // Tạo ID cho order
        private String generateOrderCode () {
            // Lấy ngày hiện tại
            LocalDate today = LocalDate.now();

            // Lấy 2 số cuối của năm
            String year = String.valueOf(today.getYear()).substring(2);

            // Lấy tháng và ngày (định dạng 4 số, ví dụ 0226)
            String monthDay = today.format(DateTimeFormatter.ofPattern("MMdd"));

            // Sinh ngẫu nhiên 5 số
            String randomDigits = String.format("%05d", new Random().nextInt(100000));

            // Tạo mã đơn hàng hoàn chỉnh
            return year + monthDay + randomDigits;
        }


        public OrderResponse getOrderById (String orderId){
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
                    orderItemResponse.setDiscountPrice(orderItemEntity.getDiscountPrice());


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

//    public List<OrderResponse> getAllOrder() {
//        List<OrderEntity> orders = orderRepository.findAll();
//
//        List<OrderResponse> orderResponses = orderMapper.toOrderResponseList(orders);
//
//
//
//        for (OrderResponse orderResponse : orderResponses) {
//
//            orders.forEach(orderEntity -> {
//                if(orderEntity.getOrderItems() != null) {
//                    List<OrderItemResponse>  orderItemResponses = new ArrayList<>();
//                    orderEntity.getOrderItems().forEach(orderItemEntity -> {
//                        OrderItemResponse orderItemResponse = new OrderItemResponse();
//                        orderItemResponse.setOrderItemId(orderItemEntity.getOrderItemId());
//                        orderItemResponse.setQuantity(orderItemEntity.getQuantity());
//                        orderItemResponse.setSubtotal(orderItemEntity.getSubtotal());
//
//                        if(orderItemEntity.getProductVariant() != null) {
//                            ProductVariantResponse productVariantResponse = new ProductVariantResponse();
//                            productVariantResponse.setId(orderItemEntity.getProductVariant().getId());
//                            productVariantResponse.setPrice(orderItemEntity.getProductVariant().getPrice());
//                            productVariantResponse.setUnit(orderItemEntity.getProductVariant().getUnit());
//                            productVariantResponse.setVolume(orderItemEntity.getProductVariant().getVolume());
//
//                            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
//                            productResponseDTO.setTitle(orderItemEntity.getProductVariant().getProduct().getTitle());
//                            productResponseDTO.setThumbnail(orderItemEntity.getProductVariant().getProduct().getThumbnail());
//                            productResponseDTO.setDiscountPercent(orderItemEntity.getProductVariant().getProduct().getDiscountPercent());
//                            productResponseDTO.setSlug(orderItemEntity.getProductVariant().getProduct().getSlug());
//                            productResponseDTO.setId(orderItemEntity.getProductVariant().getProduct().getId());
//                            productVariantResponse.setProduct(productResponseDTO);
//                            orderItemResponse.setProductVariant(productVariantResponse);
//                            orderItemResponses.add(orderItemResponse);
//                        }
//                        orderResponse.setOrderItems(orderItemResponses);
//                    });
//                }
//            });
//
//
//        }
//        return orderResponses;


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


//    }

        /* PHẦN PHÂN TRANG CHO ORDER*/

        /* PHẦN NÀY TÍCH HƠP THÊM BỘ LỌC THÔNG TIN THÔNG QUA Status và user */
        /* TÍCH HỢP THÊM TÌM KIẾM INDEX CỦA DATABASE, GIÚP TÌM NHANH HƠN TRÁNH PHẢI CHẠY NHIỀU VÒNG FOR */
        public Map<String, Object> getAllOrders(OrderStatus status, String keyword, String orderId,
                                                int page, int size, String sortBy, OrderStatus priorityStatus) {
            Map<String, Object> map = new HashMap<>();
            int p = Math.max(page - 1, 0);

            Specification<OrderEntity> spec = Specification
                    .where(OrderSpecification.isNotDeleted())
                    .and(OrderSpecification.hasStatus(status))
                    .and(OrderSpecification.hasKeyword(keyword))
                    .and(OrderSpecification.hasOrderId(orderId));

            Pageable pageable;
            Page<OrderEntity> ordersPage;

            if (sortBy != null && sortBy.equals("updatedAt")) {
                // Sort by updatedAt only
                pageable = PageRequest.of(p, size, Sort.by("updatedAt").descending());
                ordersPage = orderRepository.findAll(spec, pageable);
            } else {
                // Use default status-based sorting
                pageable = PageRequest.of(p, size);
                spec = spec.and(OrderSpecification.orderByStatusPriorityAndDate(priorityStatus));
                ordersPage = orderRepository.findAll(spec, pageable);
            }

            List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                    .map(orderEntity -> {
                        OrderResponse response = orderMapper.toOrderResponse(orderEntity);
                        if (orderEntity.getOrderItems() != null) {
                            response.setOrderItems(orderEntity.getOrderItems().stream()
                                    .map(this::createOrderItemResponse)
                                    .collect(Collectors.toList()));
                        }
                        return response;
                    })
                    .collect(Collectors.toList());

            map.put("orders", orderResponses);
            map.put("currentPage", ordersPage.getNumber() + 1);
            map.put("totalItems", ordersPage.getTotalElements());
            map.put("totalPages", ordersPage.getTotalPages());
            map.put("pageSize", ordersPage.getSize());

            return map;
        }

    private OrderItemResponse createOrderItemResponse(OrderItemEntity orderItemEntity) {
        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setOrderItemId(orderItemEntity.getOrderItemId());
        orderItemResponse.setQuantity(orderItemEntity.getQuantity());
        orderItemResponse.setSubtotal(orderItemEntity.getSubtotal());
        orderItemResponse.setDiscountPrice(orderItemEntity.getDiscountPrice());

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

        return orderItemResponse;
    }

//    Cập nhật trạng thái cho đơn hàng được chọn
        public String update (List < String > id, String orderStatus){
            try {
                OrderStatus orderStatusEnum = OrderStatus.valueOf(orderStatus);
                List<OrderEntity> orderEntities = orderRepository.findAllById(id);

                if (!orderEntities.isEmpty()) {
                    orderEntities.forEach(orderEntity -> orderEntity.setOrderStatus(orderStatusEnum));
                    orderRepository.saveAll(orderEntities);
                    return "Cập nhật trạng thái đơn hàng thành công";
                }
                return "Không tìm thấy đơn hàng nào để cập nhật";
            } catch (IllegalArgumentException e) {
                return e.getMessage(); // Hiển thị lỗi rõ ràng hơn
            }
        }

//Cập nhật trạng thái cho 1 đơn hàng
        public String update (String id, OrderRequest request){
            String orderStatus = request.getOrderStatus();

            OrderStatus orderStatusEnum = getOrderStatus(orderStatus);
            OrderEntity orderEntity = orderRepository.findById(id).orElse(null);

            if (orderEntity != null) {
                orderEntity.setOrderStatus(orderStatusEnum);
                orderRepository.save(orderEntity);
                return "Cập nhật trạng thái đơn hàng thành công";
            }
            return "Không tìm thấy đơn hàng để cập nhật";
        }

        public void deleteOrder (String orderId){
            if (!orderRepository.existsById(orderId)) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }
            orderRepository.deleteById(orderId);
        }

        public OrderResponse deleted (String orderId){
            OrderEntity orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            orderEntity.setDeleted(true);
            OrderEntity result = orderRepository.save(orderEntity);

            return orderMapper.toOrderResponse(result);
        }

        public OrderEntity getOrder (String orderId){
            return orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        }

    }
