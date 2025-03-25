package com.kit.maximus.freshskinweb.service.order;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderItemMapper;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.ProductVariantRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.repository.VoucherRepository;
import com.kit.maximus.freshskinweb.service.product.VoucherService;
import com.kit.maximus.freshskinweb.service.notification.NotificationEvent;
import com.kit.maximus.freshskinweb.service.users.EmailService;
import com.kit.maximus.freshskinweb.specification.OrderSpecification;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
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
    VoucherRepository voucherRepository;
    VoucherService voucherService;
    ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderIdResponse addOrder(OrderRequest orderRequest) {
        OrderEntity order = orderMapper.toOrderEntity(orderRequest);

        UserEntity user = null;
        if(orderRequest.getUserId() != null){
            user = userRepository.findById(orderRequest.getUserId()).orElse(null);
            order.setUser(user);
        } else {
            order.setUser(user);
        }
        // Tạo orderId duy nhất
        String orderId = generateOrderCode();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.PENDING);

        // Set payment method safely
        if (orderRequest.getPaymentMethod() != null) {
            System.out.println(orderRequest.getPaymentMethod());
            try {
                order.setPaymentMethod(PaymentMethod.valueOf(String.valueOf(orderRequest.getPaymentMethod())));
                System.out.println(order.getPaymentMethod());

            } catch (IllegalArgumentException e) {
                log.error("Invalid payment method: {}", orderRequest.getPaymentMethod());
                throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
            }
        }

        Integer totalAmount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            ProductVariantEntity variant = productVariantRepository.findById(itemRequest.getProductVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));


            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(itemRequest.getQuantity());

//            if(variant.getProduct().getDiscountPercent() != null && variant.getProduct().getDiscountPercent() > 0) {
//                BigDecimal percent = variant.getPrice()
//                        .multiply(BigDecimal.valueOf(variant.getProduct().getDiscountPercent()))
//                        .divide(BigDecimal.valueOf(100));
//                BigDecimal discount = variant.getPrice().subtract(percent);
//                BigDecimal subtotal = discount.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
//                orderItem.setSubtotal(subtotal);
//            } else {
//                orderItem.setSubtotal(variant.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
//            }

            BigDecimal discountAmount = BigDecimal.ZERO;

            if (variant.getProduct().getDiscount() != null) {
                DiscountEntity discount = variant.getProduct().getDiscount();

                if (discount.getDiscountType() == DiscountType.PERCENTAGE && discount.getDiscountPercentage() != null) {
                    discountAmount = variant.getPrice()
                            .multiply(BigDecimal.valueOf(discount.getDiscountPercentage()))
                            .divide(BigDecimal.valueOf(100));

                    // Áp dụng giới hạn giảm giá tối đa (nếu có)
                    if (discount.getMaxDiscount() != null && discountAmount.compareTo(BigDecimal.valueOf(discount.getMaxDiscount())) > 0) {
                        discountAmount = BigDecimal.valueOf(discount.getMaxDiscount());
                    }
                } else if (discount.getDiscountType() == DiscountType.FIXED_AMOUNT && discount.getDiscountAmount() != null) {
                    discountAmount = BigDecimal.valueOf(discount.getDiscountAmount());
                }
            }

// Tính giá sau giảm
            BigDecimal discountedPrice = variant.getPrice().subtract(discountAmount);

// Tính tổng tiền cho số lượng sản phẩm
            BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setSubtotal(subtotal);


            orderItem.setOrder(order);
            orderItems.add(orderItem);

            totalAmount += itemRequest.getQuantity(); // Đảm bảo kiểu số nguyên
            totalPrice = totalPrice.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        if (orderRequest.getVoucherName() != null) {
            VoucherEntity voucher = voucherRepository.findByName(orderRequest.getVoucherName())
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

            if (voucherService.validateVoucher(orderRequest.getVoucherName(), order.getTotalPrice()) == null) {
                throw new AppException(ErrorCode.VOUCHER_INVALID);
            }

            // Áp dụng giảm giá
            BigDecimal finalPrice = voucherService.applyVoucherDiscount(voucher, order.getTotalPrice());
            order.setDiscountAmount(totalPrice.subtract(finalPrice));
            order.setTotalPrice(finalPrice);

            // Giảm số lượt sử dụng voucher
            voucher.setUsed(voucher.getUsed() + 1);
            voucherRepository.save(voucher);

            // Liên kết voucher với order
            order.setVoucher(voucher);
        }


        OrderEntity savedOrder = orderRepository.save(order);

        if (user != null && order.getPaymentMethod() != null && order.getPaymentMethod().equals(PaymentMethod.CASH)) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUser(user);
            notification.setOrder(order);
            notification.setMessage("Đơn hàng " + order.getOrderId() + " đặt hàng thành công");
            eventPublisher.publishEvent(new NotificationEvent(this, notification));
        }

        return new OrderIdResponse(savedOrder.getOrderId());
    }

    public void processOrder(String orderId) {
        emailService.sendOrderConfirmationEmail(orderId); // Gọi từ bên ngoài
    }


    public void saveOrder(OrderEntity order) {
        orderRepository.save(order);
    }

    public OrderStatus getOrderStatus(String orderStatus) {
        try {
            return OrderStatus.valueOf((orderStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", orderStatus);
            throw new AppException(ErrorCode.ORDER_STATUS_INVALID);
        }
    }

    private PaymentMethod getPaymentMethod(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment method: {}", method);
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }
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
        return year + monthDay + randomDigits;
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
//                orderItemResponse.setDiscountPrice(orderItemEntity.getDiscountPrice());


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

    /*PHÂN TRANG ORDER CHO USER */
    public Map<String, Object> getUserOrders(Long userId, OrderStatus status, String keyword,
                                             String orderId, int page, int size,
                                             String sortBy, OrderStatus priorityStatus) {
        // Kiểm tra user tồn tại
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Map<String, Object> map = new HashMap<>();
        int p = Math.max(page - 1, 0);

        // Chỉ kiểm tra orderId khi nó không null
        if (orderId != null) {
            orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        }

        Specification<OrderEntity> spec = Specification
                .where(OrderSpecification.isNotDeleted())
                .and(OrderSpecification.hasUserId(userId))
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasKeyword(keyword))
                .and(orderId != null ? OrderSpecification.hasOrderId(orderId) : null);

        Pageable pageable;
        Page<OrderEntity> ordersPage;

        if (sortBy != null && sortBy.equals("updatedAt")) {
            pageable = PageRequest.of(p, size, Sort.by("updatedAt").descending());
            ordersPage = orderRepository.findAll(spec, pageable);
        } else {
            pageable = PageRequest.of(p, size);
            spec = spec.and(OrderSpecification.orderByStatusPriorityAndDate(priorityStatus));
            ordersPage = orderRepository.findAll(spec, pageable);
        }

        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(orderEntity -> {
                    OrderResponse response = orderMapper.toOrderResponse(orderEntity);

                    response.setUserId(user.getUserID());
                    response.setUsername(user.getUsername());
                    response.setTypeUser(user.getTypeUser().toString());

                    if (orderEntity.getOrderItems() != null) {
                        response.setOrderItems(orderEntity.getOrderItems().stream()
                                .map(orderItem -> OrderItemResponse.builder()
                                        .orderItemId(orderItem.getOrderItemId())
                                        .quantity(orderItem.getQuantity())
                                        .subtotal(orderItem.getSubtotal())
                                        .productVariant(ProductVariantResponse.builder()
                                                .id(orderItem.getProductVariant().getId())
                                                .price(orderItem.getProductVariant().getPrice())
                                                .volume(orderItem.getProductVariant().getVolume())
                                                .unit(orderItem.getProductVariant().getUnit())
                                                .build())
                                        .build())
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

    /*PHÂN TRANG ORDER CHO ADMIN */
    /* PHẦN NÀY TÍCH HƠP THÊM BỘ LỌC THÔNG TIN THÔNG QUA Status và user */
    /* TÍCH HỢP THÊM TÌM KIẾM INDEX CỦA DATABASE, GIÚP TÌM NHANH HƠN TRÁNH PHẢI CHẠY NHIỀU VÒNG FOR */
    public Map<String, Object> getAllOrders(OrderStatus status, String keyword, String orderId,
                                            int page, int size, String sortBy, OrderStatus priorityStatus) {
        Map<String, Object> map = new HashMap<>();
        int p = Math.max(page - 1, 0);

        // Chỉ kiểm tra orderId khi nó không null
        if (orderId != null) {
            orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        }

        Specification<OrderEntity> spec = Specification
                .where(OrderSpecification.isNotDeleted())
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasKeyword(keyword))
                .and(orderId != null ? OrderSpecification.hasOrderId(orderId) : null);

        Pageable pageable;
        Page<OrderEntity> ordersPage;

        if (sortBy != null && sortBy.equals("updatedAt")) {
            pageable = PageRequest.of(p, size, Sort.by("updatedAt").descending());
            ordersPage = orderRepository.findAll(spec, pageable);
        } else {
            pageable = PageRequest.of(p, size);
            spec = spec.and(OrderSpecification.orderByStatusPriorityAndDate(priorityStatus));
            ordersPage = orderRepository.findAll(spec, pageable);
        }

        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(orderEntity -> {
                    OrderResponse response = orderMapper.toOrderResponse(orderEntity);

                    // Xử lý trường hợp user == null ( vì cho phép user k đăng nhập vẫn có thể Order )
                    if (orderEntity.getUser() != null) {
                        response.setUserId(orderEntity.getUser().getUserID());
                        response.setUsername(orderEntity.getUser().getUsername());
                        response.setTypeUser(orderEntity.getUser().getSkinType());
                    } else {
                        response.setUserId(null);
                        response.setUsername(null);
                        response.setTypeUser(null);
                    }
                    // Ánh xạ orderItems vào response
                    List<OrderItemResponse> orderItemResponses = orderEntity.getOrderItems().stream()
                            .map(orderItem -> OrderItemResponse.builder()
                                    .orderItemId(orderItem.getOrderItemId())
                                    .quantity(orderItem.getQuantity())
                                    .subtotal(orderItem.getSubtotal())
                                    .productVariant(ProductVariantResponse.builder()
                                            .id(orderItem.getProductVariant().getId())
                                            .price(orderItem.getProductVariant().getPrice())
                                            .volume(orderItem.getProductVariant().getVolume())
                                            .unit(orderItem.getProductVariant().getUnit())
                                            .build())
                                    .build())
                            .collect(Collectors.toList());

                    response.setOrderItems(orderItemResponses);

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

    //    Cập nhật trạng thái cho đơn hàng được chọn
    public String update(List<String> id, String orderStatus) {
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
    public String update(String id, OrderRequest request) {
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

    public OrderEntity getOrder(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    //data dashboard
    public long countTotalOrders() {
        return orderRepository.count();
    }

    //đếm số đơn hàng hoàn thành
    public long countCompleted() {
        return orderRepository.countByOrderStatus(OrderStatus.COMPLETED);
    }

    public long countPending() {
        return orderRepository.countByOrderStatus(OrderStatus.PENDING);
    }

    public long countCanceled() {
        return orderRepository.countByOrderStatus(OrderStatus.CANCELED);
    }

    public String countRevenue() {
        BigDecimal totalRevenue = orderRepository.sumTotalPriceByOrderStatus(OrderStatus.COMPLETED);

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Định dạng tổng doanh thu với dấu phân cách hàng nghìn
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.getDefault());
        currencyFormat.setGroupingUsed(true);

        // Làm tròn kết quả và định dạng theo kiểu tiền tệ (sử dụng 0 thập phân)

        return currencyFormat.format(totalRevenue.setScale(0, RoundingMode.HALF_UP).doubleValue());
    }


}