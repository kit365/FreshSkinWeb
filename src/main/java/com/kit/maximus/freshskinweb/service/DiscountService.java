package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.discount.DiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.DiscountMapper;
import com.kit.maximus.freshskinweb.repository.DiscountRepository;
import com.kit.maximus.freshskinweb.specification.DiscountSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DiscountService {

    DiscountRepository discountRepository;
    DiscountMapper discountMapper;

    public boolean addDiscount(DiscountRequest request) {
        if (request == null) {
            return false;
        }

        // Kiểm tra xem mã giảm giá đã tồn tại chưa
        boolean exists = discountRepository.existsByDiscountId((request.getDiscountId()));
        if (exists) {
            throw new AppException(ErrorCode.DISCOUNT_IS_EXISTED);
        }

        // Chuyển đổi DTO thành entity
        DiscountEntity entity = discountMapper.toDiscountEntity(request);

        // Lưu vào database
        discountRepository.save(entity);

        return true;
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
}
