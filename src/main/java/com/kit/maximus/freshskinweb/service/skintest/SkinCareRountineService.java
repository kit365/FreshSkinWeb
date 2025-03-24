package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.mapper.SkinCareRoutineMapper;
import com.kit.maximus.freshskinweb.repository.SkinCareRountineRepository;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import com.kit.maximus.freshskinweb.specification.SkinCareRoutineSpecification;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SkinCareRountineService {

    SkinCareRountineRepository skinCareRountineRepository;
    SkinCareRoutineMapper skinCareRoutineMapper;
    SkinTypeRepository skinTypeRepository;

    public boolean add(SkinCareRountineRequest request) {

        SkinCareRoutineEntity skinCareRountineEntity = skinCareRoutineMapper.toEntity(request);
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(request.getSkinTypeEntity()).orElse(null);

        if(skinTypeEntity != null) {
            skinCareRountineEntity.setSkinType(skinTypeEntity);
        } else {
            skinCareRountineEntity.setSkinType(null);
        }

        skinCareRountineRepository.save(skinCareRountineEntity);
        return true;
    }

    public SkinCareRountineResponse update(Long id, SkinCareRountineRequest request) {
        SkinCareRoutineEntity skinCareRountineEntity = skinCareRountineRepository.findById(id).orElse(null);
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(request.getSkinTypeEntity()).orElse(null);

        if(skinTypeEntity != null) {
            skinCareRountineEntity.setSkinType(skinTypeEntity);
        } else {
            skinCareRountineEntity.setSkinType(skinCareRountineEntity.getSkinType());
        }

        if(skinCareRountineEntity != null) {
            skinCareRoutineMapper.updateEntity(request , skinCareRountineEntity);
            return skinCareRoutineMapper.toResponse(skinCareRountineEntity);
        }
        return null;
    }

    public boolean delete(Long id) {
        SkinCareRoutineEntity skinCareRoutineEntity = skinCareRountineRepository.findById(id).orElse(null);
        if(skinCareRoutineEntity != null) {
            skinCareRoutineEntity.setDeleted(true);
            skinCareRountineRepository.save(skinCareRoutineEntity);
            return true;
        }
        return false;
    }

    public SkinCareRountineResponse get(Long id) {
        SkinCareRoutineEntity skinCareRoutineEntity = skinCareRountineRepository.findById(id).orElse(null);
        if(skinCareRoutineEntity != null) {
            return skinCareRoutineMapper.toResponse(skinCareRoutineEntity);
        }
        return null;
    }

    public Page<SkinCareRoutineEntity> getFilteredSkinCareRoutines(Status status, String keyword, Pageable pageable) {
        Specification<SkinCareRoutineEntity> spec = Specification.where(null);

        // Add status filter if provided
        if (status != null) {
            spec = spec.and(SkinCareRoutineSpecification.filterByStatus(status));
        }

        // Add keyword filter if provided
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(SkinCareRoutineSpecification.filterByKeyword(keyword.trim()));
        }

        // Always apply sorting by updatedAt
        spec = spec.and(SkinCareRoutineSpecification.sortByUpdatedAt());

        // Always filter out deleted items
        spec = spec.and(SkinCareRoutineSpecification.isNotDeleted());

        return skinCareRountineRepository.findAll(spec, pageable);
    }
}

