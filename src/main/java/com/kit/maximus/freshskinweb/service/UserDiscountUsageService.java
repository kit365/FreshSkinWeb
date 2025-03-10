package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.user_discount_usage.CreationUserDiscountUsageRequest;
import com.kit.maximus.freshskinweb.dto.request.user_discount_usage.UpdationUserDiscountUsageRequest;
import com.kit.maximus.freshskinweb.dto.response.UserDiscountUsageResponse;
import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import com.kit.maximus.freshskinweb.entity.UserDiscountUsageEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.mapper.DiscountMapper;
import com.kit.maximus.freshskinweb.mapper.UserDiscountUsageEntityMapper;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.DiscountRepository;
import com.kit.maximus.freshskinweb.repository.UserDiscountUsageRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserDiscountUsageService {

    UserDiscountUsageRepository userDiscountUsageRepository;
    UserDiscountUsageEntityMapper userDiscountUsageEntityMapper;

    UserRepository userRepository;
    DiscountRepository discountRepository;

    public boolean addUserDiscountUsage(CreationUserDiscountUsageRequest request) {
        UserDiscountUsageEntity userDiscountUsageEntity = userDiscountUsageEntityMapper.toEntity(request);

        UserEntity userEntity = userRepository.findById(request.getUserID()).orElse(null);
        DiscountEntity discountEntity = discountRepository.findById(request.getPromoCodeID()).orElse(null);

        if (userEntity != null) {
            userDiscountUsageEntity.setUserEntity(userEntity);
        }

        if (discountEntity != null) {
            userDiscountUsageEntity.setDiscountEntity(discountEntity);
        }

        userDiscountUsageRepository.save(userDiscountUsageEntity);
        return true;

    }

    public UserDiscountUsageResponse update(Long id, UpdationUserDiscountUsageRequest request) {

        UserDiscountUsageEntity userDiscountUsageEntity = userDiscountUsageRepository.findById(id).orElse(null);

        userDiscountUsageEntityMapper.updateEntity(userDiscountUsageEntity, request);

        UserEntity userEntity = userRepository.findById(request.getUserID()).orElse(null);
        DiscountEntity discountEntity = discountRepository.findById(request.getPromoCodeID()).orElse(null);

        if (userEntity != null) {
            userDiscountUsageEntity.setUserEntity(userEntity);
        }

        if (discountEntity != null) {
            userDiscountUsageEntity.setDiscountEntity(discountEntity);
        }

        //Nếu discount được user dùng => lưu thời gian đã sử dụng
        if(request.getDiscountStatus()){
            userDiscountUsageEntity.setUsedAt(LocalDateTime.now());
        }

        return userDiscountUsageEntityMapper.toResponse(userDiscountUsageRepository.save(userDiscountUsageEntity));
    }

}
