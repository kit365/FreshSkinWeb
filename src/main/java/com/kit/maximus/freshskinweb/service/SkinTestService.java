package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.UpdationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinTestMapper;
import com.kit.maximus.freshskinweb.mapper.SkinTypeMapper;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.SkinQuestionsRepository;
import com.kit.maximus.freshskinweb.repository.SkinTestRepository;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SkinTestService implements BaseService<SkinTestResponse, CreationSkinTestRequest, UpdationSkinTestRequest, Long> {

    SkinTestRepository skinTestRepository;
    SkinTestMapper skinTestMapper;
    UserRepository userRepository;
    SkinTypeRepository skinTypeRepository;
    SkinQuestionsRepository skinQuestionsRepository;

    @Override
    public boolean add(CreationSkinTestRequest request) {
        System.out.println(request);
        SkinTestEntity entity = skinTestMapper.toSkinTestEntity(request);

        entity.setUserEntity(userRepository.findById(request.getUserEntity()).orElse(null));

        //Nhớ code thêm ràng buộc, phải làm skin test xong thì mới được set skin type
        entity.setSkinType(skinTypeRepository.findById(request.getSkinType()).orElse(null));

        //Phải có bộ đề thì mới cho làm test được
//        if(skinQuestionsRepository.findByQuestionGroup(request.getQuestionGroup()) == null && skinQuestionsRepository.findByQuestionGroup(request.getQuestionGroup()).isEmpty()) {
//            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_EXISTED);
//        } else {
//            entity.setQuestionGroup(request.getQuestionGroup());
//            skinTestRepository.save(entity);
//            return true;
//        }
        return false;
    }

    @Override
    public SkinTestResponse update(Long aLong, UpdationSkinTestRequest request) {
        SkinTestEntity entity = skinTestRepository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.SKIN_TEST_NOT_FOUND));

        skinTestMapper.updateSkinTestEntity(entity, request);

//        if(request.getQuestionGroup() != null) {
//            entity.setQuestionGroup(request.getQuestionGroup());
//        }
//
//        if (request.getUserEntity() != null) {
//            entity.setUserEntity(userRepository.findById(request.getUserEntity()).orElse(null));
//        } else {
//            entity.setUserEntity(entity.getUserEntity());
//        }
//
//        if(request.getSkinType() != null){
//            entity.setSkinType(skinTypeRepository.findById(request.getSkinType()).orElse(null));
//        } else{
//            entity.setSkinType(entity.getSkinType());
//        }

        return skinTestMapper.toSkinTestResponse(skinTestRepository.save(entity));
    }

    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status.toUpperCase());
        List<SkinTestEntity> entities = skinTestRepository.findAllById(id);
        for(SkinTestEntity entity : entities) {
            if (statusEnum.equals(Status.ACTIVE) || statusEnum.equals(Status.INACTIVE)) {
                entity.setStatus(statusEnum);
                skinTestRepository.save(entity);
            }
            if (statusEnum.equals(Status.SOFT_DELETED)) {
                entity.setDeleted(true);
                skinTestRepository.save(entity);
            }
            if (statusEnum.equals(Status.RESTORED)) {
                entity.setDeleted(false);
                skinTestRepository.save(entity);
            }

            return "Cập nhật trạng thái bài đánh giá loại da thành công";
        }
        return "Cập nhật trạng thái đánh giá loại da thất bại";
    }


    @Override
    public boolean delete(Long aLong) {
        skinTestRepository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.SKIN_TEST_NOT_FOUND));
        skinTestRepository.deleteById(aLong);
        return true;
    }

    @Override
    public boolean delete(List<Long> longs) {
        skinTestRepository.deleteAll(skinTestRepository.findAllById(longs));
        return true;
    }

    @Override
    public boolean deleteTemporarily(Long aLong) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        return false;
    }

    @Override
    public SkinTestResponse showDetail(Long aLong) {
        SkinTestEntity entity = skinTestRepository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.SKIN_TEST_NOT_FOUND));
        return skinTestMapper.toSkinTestResponse(entity);
    }

    public List<SkinTestResponse> showAll() {
    return skinTestRepository.findAll().stream().map(skinTestMapper::toSkinTestResponse).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    public Status getStatus(String status){
        try {
            return Status.valueOf(status.toUpperCase());
        }catch (IllegalStateException e){
            log.error(e.getMessage());
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }
}
