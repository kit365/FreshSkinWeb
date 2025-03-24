package com.kit.maximus.freshskinweb.service.product;

import com.kit.maximus.freshskinweb.dto.request.discount.DiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.DiscountMapper;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.repository.DiscountRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.specification.DiscountSpecification;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DiscountService {

    DiscountRepository discountRepository;
    DiscountMapper discountMapper;
    ProductRepository productRepository;
    ProductMapper productMapper;

    public DiscountResponse addDiscount(DiscountRequest request) {


        // Kiểm tra xem mã giảm giá đã tồn tại chưa
        boolean exists = discountRepository.existsByName(request.getName());
        if (exists) {
            throw new AppException(ErrorCode.DISCOUNT_IS_EXISTED);
        }

        // Chuyển đổi DTO thành entity
        DiscountEntity entity = discountMapper.toDiscountEntity(request);

        // Lưu vào database
        discountRepository.save(entity);

        return discountMapper.toDiscountResponseId(entity);
    }


    public Map<String, Object> getAllDiscounts(String name, String discountType, Boolean isGlobal, Boolean sortByUsed, Pageable pageable) {

        // Dùng Specification để tạo bộ lọc linh hoạt
        Specification<DiscountEntity> spec = Specification
                .where(DiscountSpecification.filterByName(name))
                .and(DiscountSpecification.filterByDiscountType(discountType))
                .and(DiscountSpecification.filterByIsGlobal(isGlobal))
                .and(DiscountSpecification.sortByUpdatedAtAndUsed(sortByUsed));

        Page<DiscountEntity> discountPage = discountRepository.findAll(spec, pageable);

        // Chuyển đổi sang DTO
        List<DiscountResponse> discountResponses = discountMapper.toDiscountsResponse(discountPage.getContent());

        // Chuẩn bị kết quả
        Map<String, Object> response = new HashMap<>();
        response.put("discounts", discountResponses);
        response.put("currentPage", discountPage.getNumber());
        response.put("totalItems", discountPage.getTotalElements());
        response.put("totalPages", discountPage.getTotalPages());

        return response;
    }

    public DiscountResponse getDiscount(String id) {
        var discount = discountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
        return discountMapper.toDiscountResponse(discount);

    }

    public DiscountResponse updateDiscount(String id, DiscountRequest request) {
        DiscountEntity entity = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        discountMapper.updateDiscountEntity(entity, request);
        discountRepository.save(entity);

        return discountMapper.toDiscountResponse(entity);
    }

    public boolean deleteDiscount(String id){
        DiscountEntity entity = discountRepository.findById(id).orElse(null);
        if(entity != null){
            discountRepository.delete(entity);
            return true;
        } else {
            throw new AppException(ErrorCode.DISCOUNT_NOT_FOUND);
        }
    }

    public boolean deleteDiscount(){
        discountRepository.deleteAll();
        return true;
    }

//    public boolean addProduct(String discountId, List<Long> productIds) {
//        DiscountEntity discount = discountRepository.findById(discountId)
//                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
//
//        List<ProductEntity> products = productRepository.findAllById(productIds);
//        discount.getProducts().addAll(products);
//        discountRepository.save(discount);  // Lưu thay đổi vào DB
//
//        return true;
//    }

//    public boolean applyDiscountToProducts(String id, List<Long> productIds) {
//        var discount = discountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
//
//        List<ProductEntity> products = productRepository.findAllById(productIds);
//        if (products.isEmpty()) {
//            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        for (ProductEntity product : products) {
//            product.setDiscount(discount); // Gán discount cho từng product
//            product.setDiscountPercent(discount.getDiscountPercentage());
//        }
//
//        productRepository.saveAll(products); // Lưu danh sách product sau khi cập nhật
//        return true;
//    }

//    public boolean applyDiscountToProducts(String id, List<Long> productIds) {
//        var discount = discountRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));
//
//        // 1. Kiểm tra trạng thái discount
//        if (!discount.getStatus().equals(Status.ACTIVE)) {
//            throw new AppException(ErrorCode.DISCOUNT_INACTIVE);
//        }
//
//        // 2. Kiểm tra thời gian hiệu lực của discount
//        Date now = new Timestamp(System.currentTimeMillis());
//        if (discount.getStartDate().after(now)) {
//            throw new AppException(ErrorCode.DISCOUNT_NOT_STARTED);
//        }
//        if (discount.getEndDate().before(now)) {
//            throw new AppException(ErrorCode.DISCOUNT_EXPIRED);
//        }
//
//        // 3. Kiểm tra số lần sử dụng (nếu có giới hạn)
//        if (discount.getUsageLimit() != null && discount.getUsed() >= discount.getUsageLimit()) {
//            System.out.println("Used: " + discount.getUsed() + " / Usage Limit: " + discount.getUsageLimit());
//            throw new AppException(ErrorCode.DISCOUNT_LIMIT_EXCEEDED);
//        }
//
//        List<ProductEntity> products = productRepository.findAllById(productIds);
//        if (products.isEmpty()) {
//            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        // 4. Kiểm tra sản phẩm đã có discount chưa (nếu không cho phép ghi đè)
//        for (ProductEntity product : products) {
//            if (product.getDiscount() != null) {
//                throw new AppException(ErrorCode.PRODUCT_ALREADY_HAS_DISCOUNT);
//            }
//        }
//
//        // 5. Áp dụng discount cho sản phẩm và cập nhật số lần sử dụng
//        for (ProductEntity product : products) {
//            product.setDiscount(discount);
//            product.setDiscountPercent(discount.getDiscountPercentage());
//        }
//
//        // Tăng số lần sử dụng discount
//        discount.setUsed(discount.getUsed() + products.size());
//
//        // Lưu vào database
//        productRepository.saveAll(products);
//        discountRepository.saveAndFlush(discount); // Cập nhật số lần sử dụng của discount
//
//        return true;
//    }

    @Transactional
    public boolean applyDiscountToProducts(String id, List<Long> productIds) {
        var discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        if (!discount.getStatus().equals(Status.ACTIVE)) {
            throw new AppException(ErrorCode.DISCOUNT_INACTIVE);
        }

        Date now = new Timestamp(System.currentTimeMillis());
        if (discount.getStartDate().after(now)) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_STARTED);
        }
        if (discount.getEndDate().before(now)) {
            throw new AppException(ErrorCode.DISCOUNT_EXPIRED);
        }

        List<ProductEntity> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }


        for (ProductEntity product : products) {
            if (product.getDiscount() != null) {
                throw new AppException(ErrorCode.PRODUCT_ALREADY_HAS_DISCOUNT);
            }
        }

        // **Cập nhật `used` trước khi áp dụng discount**
        int updatedRows = discountRepository.incrementUsage(id, productIds.size());
        if (updatedRows == 0) {
            throw new AppException(ErrorCode.DISCOUNT_LIMIT_EXCEEDED);
        }

        for (ProductEntity product : products) {
            product.setDiscount(discount);
            product.setDiscountPercent(discount.getDiscountPercentage());
        }

        productRepository.saveAll(products);

        return true;
    }


    public boolean removeDiscountFromProducts(String id, List<Long> productIds) {
        var discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        List<ProductEntity> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        for (ProductEntity product : products) {
            if (product.getDiscount() != null && product.getDiscount().equals(discount)) {
                product.setDiscount(null); // Xóa discount khỏi product
            }
        }

        productRepository.saveAll(products);
        return true;
    }
}
