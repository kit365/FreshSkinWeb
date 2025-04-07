package com.kit.maximus.freshskinweb.service.order;

import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.orderItem.OrderItemRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderItemMapper;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.mapper.VoucherMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.ProductVariantRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.repository.VoucherRepository;
import com.kit.maximus.freshskinweb.repository.search.ProductSearchRepository;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import com.kit.maximus.freshskinweb.service.product.VoucherService;
import com.kit.maximus.freshskinweb.service.notification.NotificationEvent;
import com.kit.maximus.freshskinweb.service.users.EmailService;
import com.kit.maximus.freshskinweb.specification.OrderSpecification;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import com.kit.maximus.freshskinweb.utils.PaymentMethod;
import com.kit.maximus.freshskinweb.utils.PaymentStatus;
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
    VoucherMapper voucherMapper;
    ApplicationEventPublisher eventPublisher;
    ProductService productService;

    @Transactional
    public OrderIdResponse addOrder(OrderRequest orderRequest) {
        OrderEntity order = orderMapper.toOrderEntity(orderRequest);

        UserEntity user = null;
        if (orderRequest.getUserId() != null) {
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

                //Phương thức mà là CASH => TRẠNG THÁI LÀ ĐANG CHỜ THANH TOÁN
                if (orderRequest.getPaymentMethod().equals(PaymentMethod.CASH)) {
                    order.setPaymentStatus(PaymentStatus.PENDING);
                }

            } catch (IllegalArgumentException e) {
                log.error("Invalid payment method: {}", orderRequest.getPaymentMethod());
                throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
            }
        }

        Integer totalAmount = 0;
        BigDecimal totalPrice = orderRequest.getTotalPrice().subtract(orderRequest.getPriceShipping());
        System.out.println(orderRequest.getTotalPrice());
        System.out.println(totalPrice);
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            ProductVariantEntity variant = productVariantRepository.findById(itemRequest.getProductVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));


            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(itemRequest.getQuantity());
            BigDecimal discountAmount = BigDecimal.ZERO;

            if (variant.getProduct().getDiscountPercent() != null) {
                discountAmount = variant.getPrice()
                        .multiply(BigDecimal.valueOf(variant.getProduct().getDiscountPercent())
                                .divide(BigDecimal.valueOf(100)));

            }

// Tính giá sau giảm
            BigDecimal discountedPrice = variant.getPrice().subtract(discountAmount);

// Tính tổng tiền cho số lượng sản phẩm
            BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setSubtotal(subtotal);


            orderItem.setOrder(order);
            orderItems.add(orderItem);

            totalAmount += itemRequest.getQuantity(); // Đảm bảo kiểu số nguyên
//            totalPrice = totalPrice.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setTotalPrice(totalPrice.add(orderRequest.getPriceShipping()));
        System.out.println(totalPrice.add(orderRequest.getPriceShipping()));
        System.out.println(totalPrice);
        order.setOrderItems(orderItems);
        order.setPriceShipping(orderRequest.getPriceShipping());

        if (orderRequest.getVoucherName() != null) {
            if (orderRequest.getVoucherName().isBlank()) {
                order.setVoucher(null);
            } else {
                VoucherEntity voucher = voucherRepository.findByName(orderRequest.getVoucherName())
                        .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

                if (voucherService.validateVoucher(orderRequest.getVoucherName(), order.getTotalPrice()) == null) {
                    throw new AppException(ErrorCode.VOUCHER_INVALID);
                }

                // Áp dụng giảm giá
                BigDecimal finalPrice = voucherService.applyVoucherDiscount(voucher, totalPrice);
                order.setDiscountAmount(totalPrice.subtract(finalPrice));
                System.out.println("finalPrice:" + finalPrice);
                System.out.println("totalPrice:" + totalPrice);
                System.out.println("totalPrice.subtract(finalPrice):" + totalPrice.subtract(finalPrice));
                order.setTotalPrice(finalPrice.add(orderRequest.getPriceShipping()));
                System.out.println("finalPrice.add(orderRequest.getPriceShipping():" + finalPrice.add(orderRequest.getPriceShipping()));

                // Giảm số lượt sử dụng voucher
                voucher.setUsed(voucher.getUsed() + 1);
                voucherRepository.save(voucher);

                // Liên kết voucher với order
                order.setVoucher(voucher);


            }

        }


        OrderEntity savedOrder = orderRepository.save(order);

        if (!savedOrder.getOrderItems().isEmpty()) {
            savedOrder.getOrderItems().forEach(orderItem -> {
                if (orderItem.getProductVariant() != null) {
                    productService.updateStock(
                            orderItem.getProductVariant().getId(),
                            orderItem.getProductVariant().getProduct().getId(),
                            orderItem.getQuantity()
                    );
                }
            });
        }


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
        log.info(orderStatus.toString());
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
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);

        // Map OrderItems và thêm thông tin product
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

            orderResponse.setPriceShipping(order.getPriceShipping());
            orderResponse.setOrderItems(orderItemResponses);
        }

        // Map thông tin voucher đầy đủ nếu có
        if (order.getVoucher() != null) {
            VoucherResponse voucherResponse = voucherMapper.toVoucherResponse(order.getVoucher());
            orderResponse.setVoucher(voucherResponse);
        }
        // Map thông tin người dùng
        return orderResponse;
    }


    //Tra cứu đơn hàng theo SĐT or Email or Both
    public List<OrderResponse> getOrdersByEmailAndPhoneNumber(String email, String phone) {
        List<OrderEntity> orders = null;

        if (email != null && phone != null) {
            orders = orderRepository.findAllByEmailAndPhoneNumber(email, phone, Sort.by(Sort.Direction.DESC, "updatedAt"));
        } else if (email != null) {
            orders = orderRepository.findAllByEmail(email);
        } else if (phone != null) {
            orders = orderRepository.findAllByPhoneNumber(phone);
        }

        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort orders by updatedAt
        orders.sort(Comparator.comparing(OrderEntity::getUpdatedAt).reversed());

        return orders.stream().map(order -> {
            OrderResponse orderResponse = orderMapper.toOrderResponse(order);

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
        }).collect(Collectors.toList());
    }


    /*PHÂN TRANG ORDER CHO USER */
    public List<OrderResponse> getUserOrders(Long userId) {
        // Kiểm tra user tồn tại
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tạo Specification đơn giản chỉ lọc theo userId và không bị xóa
        Specification<OrderEntity> spec = Specification
                .where(OrderSpecification.isNotDeleted())
                .and(OrderSpecification.hasUserId(userId));

        // Sắp xếp theo updatedAt giảm dần
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");

        List<OrderEntity> orders = orderRepository.findAll(spec, sort);

        return orders.stream()
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
    }


    /*PHÂN TRANG ORDER CHO ADMIN */
    /* PHẦN NÀY TÍCH HƠP THÊM BỘ LỌC THÔNG TIN THÔNG QUA Status và user */
    /* TÍCH HỢP THÊM TÌM KIẾM INDEX CỦA DATABASE, GIÚP TÌM NHANH HƠN TRÁNH PHẢI CHẠY NHIỀU VÒNG FOR */
    public Map<String, Object> getAllOrders(OrderStatus status, String keyword, String orderId,
                                            int page, int size, String sortBy, OrderStatus priorityStatus) {
        Map<String, Object> map = new HashMap<>();
        int p = Math.max(page - 1, 0);

        if (orderId != null) {
            orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        }

        Specification<OrderEntity> spec = Specification
                .where(OrderSpecification.isNotDeleted())
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasKeyword(keyword))
                .and(orderId != null ? OrderSpecification.hasOrderId(orderId) : null);

        Pageable pageable = sortBy != null && sortBy.equals("updatedAt")
                ? PageRequest.of(p, size, Sort.by("updatedAt").descending())
                : PageRequest.of(p, size);

        if (sortBy == null) {
            spec = spec.and(OrderSpecification.orderByStatusPriorityAndDate(priorityStatus));
        }

        Page<OrderEntity> ordersPage = orderRepository.findAll(spec, pageable);

        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(orderEntity -> {
                    OrderResponse response = orderMapper.toOrderResponse(orderEntity);

                    if (orderEntity.getUser() != null) {
                        response.setUserId(orderEntity.getUser().getUserID());
                        response.setUsername(orderEntity.getUser().getUsername());
                        response.setTypeUser(orderEntity.getUser().getSkinType());
                    }

                    response.setOrderItems(orderEntity.getOrderItems().stream()
                            .map(orderItem -> OrderItemResponse.builder()
                                    .orderItemId(orderItem.getOrderItemId())
                                    .quantity(orderItem.getQuantity())
                                    .subtotal(orderItem.getSubtotal())
                                    .productVariant(mapProductVariant(orderItem.getProductVariant()))
                                    .build())
                            .collect(Collectors.toList()));

                    return response;
                })
                .collect(Collectors.toList());

        return buildPaginationResponse(map, ordersPage, orderResponses);
    }

    private ProductVariantResponse mapProductVariant(ProductVariantEntity variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .price(variant.getPrice())
                .volume(variant.getVolume())
                .unit(variant.getUnit())
                .product(variant.getProduct() != null ? mapProduct(variant.getProduct()) : null)
                .build();
    }

    private ProductResponseDTO mapProduct(ProductEntity product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .thumbnail(product.getThumbnail())
                .discountPercent(product.getDiscountPercent())
                .slug(product.getSlug())
                .build();
    }

    private Map<String, Object> buildPaginationResponse(Map<String, Object> map,
                                                        Page<OrderEntity> page,
                                                        List<OrderResponse> content) {
        map.put("orders", content);
        map.put("currentPage", page.getNumber() + 1);
        map.put("totalItems", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
        map.put("pageSize", page.getSize());
        return map;
    }

    //    Cập nhật trạng thái cho đơn hàng được chọn
    public String update(List<String> id, String orderStatus) {
        try {
            OrderStatus orderStatusEnum = OrderStatus.valueOf(orderStatus);
            List<OrderEntity> orderEntities = orderRepository.findAllById(id);

            if (!orderEntities.isEmpty()) {
                for (OrderEntity orderEntity : orderEntities) {
                    // Nếu thanh toán bằng tiền mặt

                    if (orderEntity.getPaymentMethod().equals(PaymentMethod.CASH)) {


                        // Nếu đơn hàng được giao => thanh toán thành công
                        if (orderStatusEnum.equals(OrderStatus.COMPLETED)) {
                            orderEntity.setPaymentStatus(PaymentStatus.PAID);
                            // Chưa được giao => thanh toán thất bại
                        } else if (orderStatusEnum.equals(OrderStatus.CANCELED)) {
                            orderEntity.setPaymentStatus(PaymentStatus.FAILED);
                        }
                    }

                    if (orderStatusEnum.equals(OrderStatus.CANCELED)) {
                        if (orderEntity.getPaymentMethod().equals(PaymentMethod.QR)) {
                            orderEntity.setPaymentStatus(PaymentStatus.REFUNDED);
                        }

                        orderEntity.getOrderItems().forEach(orderItem -> productService.updateStockCancel(
                                orderItem.getProductVariant().getId(),
                                orderItem.getProductVariant().getProduct().getId(),
                                orderItem.getQuantity()
                        ));
                    }
                    orderEntity.setOrderStatus(orderStatusEnum);
                }
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
            // Nếu thanh toán bằng QR => Khi đơn hang bị hủy => bên QR sẽ cập nhật trạng thái thanh toán(Refunded)
            if (orderEntity.getPaymentMethod().equals(PaymentMethod.QR)) {
                if (orderStatusEnum.equals(OrderStatus.CANCELED)) {
                    orderEntity.setPaymentStatus(PaymentStatus.REFUNDED);
                }
            }

            // Nếu thanh toán bằng tiền mặt
            if (orderEntity.getPaymentMethod().equals(PaymentMethod.CASH)) {


                // Nếu đơn hàng được giao => thanh toán thành công
                if (orderStatusEnum.equals(OrderStatus.COMPLETED)) {
                    orderEntity.setPaymentStatus(PaymentStatus.PAID);
                    // Chưa được giao => thanh toán thất bại
                } else if (orderStatusEnum.equals(OrderStatus.CANCELED)) {
                    orderEntity.setPaymentStatus(PaymentStatus.FAILED);
                }
            }

            orderEntity.setOrderStatus(orderStatusEnum);
            orderRepository.save(orderEntity);

            if (orderStatusEnum.equals(OrderStatus.CANCELED)) {
                orderEntity.getOrderItems().forEach(orderItem -> productService.updateStockCancel(
                        orderItem.getProductVariant().getId(),
                        orderItem.getProductVariant().getProduct().getId(),
                        orderItem.getQuantity()
                ));
            }

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

    public long countShipping() {
        return orderRepository.countByOrderStatus(OrderStatus.DELIVERING);
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

    public List<OrderResponse> getRevenueByDate() {
        List<Object[]> results = orderRepository.getTotalPriceByDate(OrderStatus.COMPLETED);
        List<OrderResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            BigDecimal totalPriceDecimal = (BigDecimal) result[0];
            Long totalPrice = totalPriceDecimal.longValue();
            Date date = (Date) result[1];

            OrderResponse response = OrderResponse.builder()
                    .totalAmount(totalPrice)
                    .orderDate(date)
                    .build();

            responses.add(response);
        }

        return responses;
    }


}