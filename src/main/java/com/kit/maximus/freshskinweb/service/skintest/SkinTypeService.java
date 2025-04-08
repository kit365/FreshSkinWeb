package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.skin_type.CreateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type.UpdateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeResponse;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinTypeMapper;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SkinTypeService {

    SkinTypeRepository skinTypeRepository;

    SkinTypeMapper skinTypeMapper;

    public boolean add(CreateSkinTypeRequest request) {
        SkinTypeEntity skinTypeEntity = skinTypeMapper.toSkinTypeEntity(request);
        skinTypeRepository.save(skinTypeEntity);
        return true;
    }

    public SkinTypeResponse update(Long id, UpdateSkinTypeRequest request) {
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(id).orElse(null);
        skinTypeMapper.updateSkinType(skinTypeEntity, request);
        return skinTypeMapper.toSkinTypeResponse(skinTypeRepository.save(skinTypeEntity));
    }

    public boolean delete(Long id) {
        SkinTypeEntity skinType = skinTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));

        //Set null skin type cho bảng trung gian
        // Product là thằng tạo bảng trung gian => set skintype == null tại product có skintype ID mình truyền vào
        skinType.getProducts().forEach(product -> {
            product.setSkinTypes(null);
        });

        skinTypeRepository.deleteById(id);
        return true;
    }

    public boolean delete(List<Long> ids) {
        List<SkinTypeEntity> skinTypeEntities = skinTypeRepository.findAllById(ids);
        skinTypeRepository.deleteAll(skinTypeEntities);
        return true;
    }

    public List<SkinTypeResponse> showAll() {
        return skinTypeRepository.findAll().stream().map(skinTypeMapper::toSkinTypeResponse).collect(Collectors.toList());
    }

    public SkinTypeResponse searchById(Long id) {
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));
        return skinTypeMapper.toSkinTypeResponse(skinTypeEntity);
    }
}
