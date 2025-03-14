package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
//import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinResultSearchRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.UpdationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinTestMapper;
import com.kit.maximus.freshskinweb.repository.*;
//import com.kit.maximus.freshskinweb.specification.SkinTestSpecification;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SkinTestService {

    SkinTestRepository skinTestRepository;
    SkinTestMapper skinTestMapper;
    UserRepository userRepository;
    SkinTypeRepository skinTypeRepository;
    SkinQuestionsRepository skinQuestionsRepository;
    QuestionGroupRepository questionGroupRepository;

    public boolean add(CreationSkinTestRequest request){
        SkinTestEntity skinTestEntity = skinTestMapper.toSkinTestEntity(request);
        QuestionGroupEntity questionGroupEntity = questionGroupRepository.findById(request.getQuestionGroup()).orElse(null);
        UserEntity userEntity = userRepository.findById(request.getUser()).orElse(null);
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findById(request.getSkinType()).orElse(null);
        SkinQuestionsEntity skinQuestionsEntity = skinQuestionsRepository.findById(request.getQuestionGroup()).orElse(null);

        if(userEntity == null){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        } else {
            skinTestEntity.setUser(userEntity);
        }
        if(skinTypeEntity == null){
            throw new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND);
        } else {
            skinTestEntity.setSkinType(skinTypeEntity);
        }

        if(questionGroupEntity == null){
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND);
        } else if (questionGroupEntity.getQuestions().size() == 0){
            throw new AppException(ErrorCode.SKIN_QUESTIONS_NOT_FOUND);
        } else {
            skinTestEntity.setQuestionGroup(questionGroupRepository.findById(request.getQuestionGroup()).orElse(null));
        }

        skinTestRepository.save(skinTestEntity);
        return true;
    }

    public boolean update(Long id, UpdationSkinTestRequest request){
        SkinTestEntity skinTestEntity = skinTestRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SKIN_TEST_NOT_FOUND));

        if(request.getUser() != null){
            skinTestEntity.setUser(userRepository.findById(request.getUser()).orElse(null));
        } else {
            skinTestEntity.setUser(skinTestEntity.getUser());
        }

        if(request.getSkinType() != null){
            skinTestEntity.setSkinType(skinTypeRepository.findById(request.getSkinType()).orElse(null));
        } else {
            skinTestEntity.setSkinType(skinTestEntity.getSkinType());
        }

        if(request.getQuestionGroup() != null){
            skinTestEntity.setQuestionGroup(questionGroupRepository.findById(request.getQuestionGroup()).orElse(null));
        } else {
            skinTestEntity.setQuestionGroup(skinTestEntity.getQuestionGroup());
        }

        if( request.getStatus() != null){
            Status status = getStatus(request.getStatus());
            if(status == Status.ACTIVE || status == Status.INACTIVE){
                skinTestEntity.setStatus(status);
            } else if(status == Status.SOFT_DELETED){
                skinTestEntity.setDeleted(true);
            } else if (status == Status.RESTORED){
                skinTestEntity.setDeleted(false);
            }
        }

        skinTestMapper.updateSkinTestEntity(skinTestEntity, request);

        skinTestRepository.save(skinTestEntity);
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

    public SkinTestResponse get(Long id){
        SkinTestEntity skinTestEntity = skinTestRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SKIN_TEST_NOT_FOUND));
        return skinTestMapper.toSkinTestResponse(skinTestEntity);
    }

//    public Page<SkinTestResponse> getAll(SkinResultSearchRequest request) {
//        int page = request.getPage() != null ? request.getPage() : 0;
//        int size = request.getSize() != null ? request.getSize() : 10;
//        String sortDir = request.getSortDirection() != null ? request.getSortDirection() : "desc";
//
//        // Tạo Pageable với sắp xếp theo updatedAt
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), "updatedAt");
//        PageRequest pageRequest = PageRequest.of(page, size, sort);
//
//        // Thực hiện truy vấn với Specification
//        Page<SkinTestEntity> skinTests = skinTestRepository.findAll(
//                SkinTestSpecification.withFilters(request),
//                pageRequest
//        );
//
//        // Chuyển đổi kết quả sang DTO
//        return skinTests.map(skinTestMapper::toSkinTestResponse);
//    }

    public boolean delete(Long id){
        SkinTestEntity skinTestEntity = skinTestRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SKIN_TEST_NOT_FOUND));
        skinTestRepository.delete(skinTestEntity);
        return true;
    }

}
