package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.CreationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeScoreRangeResponse;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeScoreRangeEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinTypeScoreRangeMapper;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import com.kit.maximus.freshskinweb.repository.SkinTypeScoreRangeRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SkinTypeScoreRangeService {

    SkinTypeScoreRangeRepository repository;
    SkinTypeScoreRangeMapper mapper;

    SkinTypeRepository skinTypeRepository;


    public boolean add(CreationSkinTypeScoreRangeRequest request) {
        // Kiểm tra nếu có id và id đã tồn tại
        if (request.getId() != null) {
            SkinTypeScoreRangeEntity existingEntity = repository.findById(request.getId()).orElse(null);
            if (existingEntity != null) {
                // Cập nhật thông tin cho entity hiện có
                existingEntity.setMinScore(request.getMinScore());
                existingEntity.setMaxScore(request.getMaxScore());

                SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(request.getSkinType()).orElse(null);
                if (skinTypeEntity != null) {
                    existingEntity.setSkinType(skinTypeEntity);
                    repository.save(existingEntity);
                    return true;
                } else {
                    existingEntity.setSkinType(null);
                    return false;
                }
            }
        }

        // Xử lý thêm mới nếu không có id hoặc id chưa tồn tại
        SkinTypeScoreRangeEntity skinTypeScoreRangeEntity = mapper.toSkinTypeScoreRangeEntity(request);
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(request.getSkinType()).orElse(null);

        if (skinTypeEntity != null) {
            skinTypeScoreRangeEntity.setSkinType(skinTypeEntity);
            repository.save(skinTypeScoreRangeEntity);
            return true;
        } else {
            skinTypeScoreRangeEntity.setSkinType(null);
            return false;
        }
    }

    public Map<String, Object> getAll(String status, String skinType,
                                      int page, int size, String sortDir) {
        Map<String, Object> response = new HashMap<>();
        int pageNumber = Math.max(page - 1, 0);

        // Convert status string to enum if present
        Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Status.valueOf(status.toUpperCase());
                if (statusEnum != Status.ACTIVE && statusEnum != Status.INACTIVE) {
                    throw new AppException(ErrorCode.INVALID_STATUS);
                }
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_STATUS);
            }
        }

        Specification<SkinTypeScoreRangeEntity> spec = Specification
                .where((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (status != null) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    if (skinType != null && !skinType.trim().isEmpty()) {
                        predicates.add(cb.like(
                                cb.lower(root.get("skinType").get("type")),
                                "%" + skinType.toLowerCase().trim() + "%"
                        ));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                });

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC, "updatedAt");

        Pageable pageable = PageRequest.of(pageNumber, size, sort);
        Page<SkinTypeScoreRangeEntity> resultPage = repository.findAll(spec, pageable);

        List<Map<String, Object>> items = resultPage.getContent().stream()
                .map(entity -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", entity.getId());
                    item.put("minScore", entity.getMinScore());
                    item.put("maxScore", entity.getMaxScore());
                    item.put("type", entity.getSkinType().getType());
                    item.put("description", entity.getSkinType().getDescription());
                    return item;
                })
                .collect(Collectors.toList());

        response.put("items", items);
        response.put("currentPage", resultPage.getNumber() + 1);
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("pageSize", resultPage.getSize());

        return response;
    }

    public Map<String, Object> getTrash(String status, String skinType,
                                        int page, int size, String sortDir) {
        Map<String, Object> response = new HashMap<>();
        int pageNumber = Math.max(page - 1, 0);

        // Convert status string to enum if present
        Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Status.valueOf(status.toUpperCase());
                if (statusEnum != Status.ACTIVE && statusEnum != Status.INACTIVE) {
                    throw new AppException(ErrorCode.INVALID_STATUS);
                }
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_STATUS);
            }
        }

        Specification<SkinTypeScoreRangeEntity> spec = Specification
                .where((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("deleted"), true));

                    if (status != null) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    if (skinType != null && !skinType.trim().isEmpty()) {
                        predicates.add(cb.like(
                                cb.lower(root.get("skinType").get("type")),
                                "%" + skinType.toLowerCase().trim() + "%"
                        ));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                });

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC, "updatedAt");

        Pageable pageable = PageRequest.of(pageNumber, size, sort);
        Page<SkinTypeScoreRangeEntity> resultPage = repository.findAll(spec, pageable);

        List<Map<String, Object>> items = resultPage.getContent().stream()
                .map(entity -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", entity.getId());
                    item.put("minScore", entity.getMinScore());
                    item.put("maxScore", entity.getMaxScore());
                    item.put("type", entity.getSkinType().getType());
                    item.put("description", entity.getSkinType().getDescription());
                    return item;
                })
                .collect(Collectors.toList());

        response.put("items", items);
        response.put("currentPage", resultPage.getNumber() + 1);
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("pageSize", resultPage.getSize());

        return response;
    }

    public boolean update(CreationSkinTypeScoreRangeRequest request){
        SkinTypeScoreRangeEntity entity = mapper.toSkinTypeScoreRangeEntity(request);
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(request.getSkinType()).orElse(null);
        if (skinTypeEntity != null) {
            entity.setSkinType(skinTypeEntity);
            repository.save(entity);
            return true;
        } else {
            entity.setSkinType(null);
            throw new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND);
        }
    }

    public SkinTypeScoreRangeResponse getDetail(Long id){
        SkinTypeScoreRangeEntity skinTypeScoreRangeEntity = getEntity(id);
        SkinTypeScoreRangeResponse skinTypeScoreRangeResponse = mapper.toSkinTypeScoreRangeResponse(skinTypeScoreRangeEntity);
        skinTypeScoreRangeResponse.setType(skinTypeScoreRangeEntity.getSkinType().getType());
        skinTypeScoreRangeResponse.setDescription(skinTypeScoreRangeEntity.getSkinType().getDescription());

        if(skinTypeScoreRangeResponse != null){
            return skinTypeScoreRangeResponse;
        } else {
            throw new AppException(ErrorCode.SCORE_RANGE_NOT_FOUND);
        }
    }

    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<SkinTypeScoreRangeEntity> skinTypeScoreRangeEntities = repository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            skinTypeScoreRangeEntities.forEach(skinTypeScoreRangeEntity -> skinTypeScoreRangeEntity.setStatus(statusEnum));
            repository.saveAll(skinTypeScoreRangeEntities);
            return "Cập nhật trạng thái mức điểm loại da thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            skinTypeScoreRangeEntities.forEach(skinTypeScoreRangeEntity -> skinTypeScoreRangeEntity.setDeleted(true));
            repository.saveAll(skinTypeScoreRangeEntities);
            return "Cập nhật trạng thái mức điểm loại da thành công";
        } else if (statusEnum == Status.RESTORED) {
            skinTypeScoreRangeEntities.forEach(skinTypeScoreRangeEntity -> skinTypeScoreRangeEntity.setDeleted(false));
            repository.saveAll(skinTypeScoreRangeEntities);
            return "Cập nhật trạng thái mức điểm loại da thành công";
        }
        return "Cập nhật thất bại";
    }

    public boolean delete(Long id){
        SkinTypeScoreRangeEntity skinTypeScoreRangeEntity = getEntity(id);
        if(skinTypeScoreRangeEntity != null) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean deleteSelectedPart(List<Long> ids){
        if(ids != null) {
            List<SkinTypeScoreRangeEntity> skinTypeScoreRangeEntities = repository.findAllById(ids);

            skinTypeScoreRangeEntities.forEach(productEntity -> {
            });
            repository.deleteAll(skinTypeScoreRangeEntities);
            return true;
        } else {
            throw new AppException(ErrorCode.SCORE_RANGE_NOT_FOUND);
        }
    }

    public boolean deleteTemporarily(Long id) {
        SkinTypeScoreRangeEntity skinTypeScoreRangeEntity = getEntity(id);
        log.info("Delete temporarily : {}", id);
        skinTypeScoreRangeEntity.setDeleted(true);
        repository.save(skinTypeScoreRangeEntity);
        return true;
    }

    public boolean restore(Long id) {
        SkinTypeScoreRangeEntity skinTypeScoreRangeEntity = getEntity(id);
        skinTypeScoreRangeEntity.setDeleted(false);
        repository.save(skinTypeScoreRangeEntity);
        return true;
    }

    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    public SkinTypeScoreRangeEntity getEntity(Long id){
        SkinTypeScoreRangeEntity entity = repository.findById(id).orElse(null);
        if(entity == null){
            throw new AppException(ErrorCode.SCORE_RANGE_NOT_FOUND);
        } else {
            return entity;
        }
    }
}
