package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.discount.CreationDiscountRequest;
import com.kit.maximus.freshskinweb.dto.request.discount.UpdationtionDiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.DiscountMapper;
import com.kit.maximus.freshskinweb.repository.DiscountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DiscountService {

    DiscountRepository discountRepository;
    DiscountMapper discountMapper;

    public boolean addDiscount(CreationDiscountRequest request){
        if(request != null){
            discountRepository.save(discountMapper.toDiscountEntity(request));
            return true;
        }
        return false;
    }

    public DiscountResponse getDiscount(Long id){
        return discountRepository.findById(id).map(discountMapper::toDiscountResponse).orElse(null);
    }

    public List<DiscountResponse> getAllDiscounts(){
        List<DiscountResponse> result = discountRepository.findAll().stream().map(discountMapper::toDiscountResponse).collect(Collectors.toList());
        return result;
    }

    public DiscountResponse updateDiscount(Long id, UpdationtionDiscountRequest request){
        DiscountEntity entity = discountRepository.findById(id).orElse(null);
        if(entity != null){
            discountMapper.updateDiscountEntity(entity, request);
        return discountMapper.toDiscountResponse(entity);
        } else {
            throw new AppException(ErrorCode.DISCOUNT_NOT_FOUND);
        }
    }

    public boolean deleteDiscount(Long id){
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
