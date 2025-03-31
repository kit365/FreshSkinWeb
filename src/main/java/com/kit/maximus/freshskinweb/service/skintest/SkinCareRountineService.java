package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.UpdationSkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.mapper.SkinCareRoutineMapper;
import com.kit.maximus.freshskinweb.mapper.SkinTypeMapper;
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
    SkinTypeMapper skinTypeMapper;

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

    public Boolean update(Long id, UpdationSkinCareRountineRequest request) {
        SkinCareRoutineEntity skinCareRountineEntity = skinCareRountineRepository.findById(id).orElse(null);

        if(skinCareRountineEntity != null) {
            skinCareRoutineMapper.updateEntity(request , skinCareRountineEntity);
            skinCareRountineRepository.save(skinCareRountineEntity);
            return true;
        }
        return false;
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
        if (skinCareRoutineEntity != null) {
            SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(skinCareRoutineEntity);
            if (skinCareRoutineEntity.getSkinType() != null) {
                response.setSkinTypeEntity(skinTypeMapper.toSkinTypeResponse(skinCareRoutineEntity.getSkinType()));
            }
            return response;
        }
        return null;
    }

    public SkinCareRountineResponse toResponse(SkinCareRoutineEntity entity) {
        if (entity == null) {
            return null;
        }
        SkinCareRountineResponse response = new SkinCareRountineResponse();
        response.setId(entity.getId());
        response.setRountine(entity.getRountine());
        response.setSkinTypeEntity( skinTypeMapper.toSkinTypeResponse(entity.getSkinType()));
        // Các trường khác
        return response;
    }



    public Page<SkinCareRountineResponse> getFilteredSkinCareRoutines(Status status, String keyword, Pageable pageable) {
        Specification<SkinCareRoutineEntity> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and(SkinCareRoutineSpecification.filterByStatus(status));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(SkinCareRoutineSpecification.filterByKeyword(keyword.trim()));
        }

        spec = spec.and(SkinCareRoutineSpecification.sortByUpdatedAt());
        spec = spec.and(SkinCareRoutineSpecification.isNotDeleted());

        Page<SkinCareRoutineEntity> entities = skinCareRountineRepository.findAll(spec, pageable);
        return entities.map(entity -> {
            if (entity.getSkinType() == null) {
                entity.setSkinType(new SkinTypeEntity()); // Hoặc giá trị mặc định khác
            }
            return toResponse(entity);
        });
    }
}

