package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.discount.CreationDiscountRequest;
import com.kit.maximus.freshskinweb.dto.request.discount.UpdationtionDiscountRequest;
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

    public boolean addDiscount(CreationDiscountRequest request){
        if(request != null){
            DiscountEntity entity = discountMapper.toDiscountEntity(request);

            //Kiểm tra trong danh sách có tồn tại mã giảm giá trước đó không
            List<DiscountEntity> discountEntities = discountRepository.findAll();
            for(DiscountEntity discountEntity : discountEntities){
                //Nếu có quăng lỗi đã tồn tại
                if(request.getPromoCode().equals(discountEntity.getPromoCode())){
                    throw new AppException(ErrorCode.DISCOUNT_IS_EXISTED);
                }
            }
            entity.setPromoCode(request.getPromoCode());
            discountRepository.save(entity);
            return true;
        }
        return false;
    }

    public Map<String, Object> getAllDiscounts(String promoCode, String discountType, Boolean isGlobal, Boolean sortByUsed, Pageable pageable) {

        // Dùng Specification để tạo bộ lọc linh hoạt
        Specification<DiscountEntity> spec = Specification
                .where(DiscountSpecification.filterByPromoCode(promoCode))
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
        return discountMapper.toDiscountResponse(
                discountRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND))
        );
    }

    public DiscountResponse updateDiscount(String id, UpdationtionDiscountRequest request) {
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
