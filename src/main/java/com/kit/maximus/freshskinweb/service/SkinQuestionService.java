package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.UpdateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinQuestionsResponse;
import com.kit.maximus.freshskinweb.entity.SkinAnswerEntity;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinQuestionsMapper;
import com.kit.maximus.freshskinweb.repository.SkinAnswerRepository;
import com.kit.maximus.freshskinweb.repository.SkinQuestionsRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SkinQuestionService implements BaseService<SkinQuestionsResponse, CreateSkinQuestionsRequest, UpdateSkinQuestionsRequest, Long>{

    SkinQuestionsRepository repository;
    SkinQuestionsMapper mapper;
    SkinAnswerRepository answerRepository;

    @Override
    public boolean add(CreateSkinQuestionsRequest request) {
        return true;
    }

    public SkinAnswerEntity getAnswer(Long id){
        return answerRepository.findById(id).orElse(null);
    }

    @Override
    public SkinQuestionsResponse update(Long aLong, UpdateSkinQuestionsRequest request) {
        SkinQuestionsEntity entity = getSkinQuestions(aLong);

        if(!request.getSkinAnswers().isEmpty()){
            //Chuyển đổi answer thành MAP
            Map<String, SkinAnswerEntity> requestList = listAnswerstoMap(request.getSkinAnswers());
            Map<String, SkinAnswerEntity> currentList = listAnswerstoMap(entity.getSkinAnswers());

            //Xét nếu answer có trong request, mà entity ko có => add mới
            // Nếu entity có => ghi đè lại thằng cũ
            for (SkinAnswerEntity answerEntityList : requestList.values()) {
                if(request.getSkinAnswers().contains(answerEntityList.getSkinOption())){
                    SkinAnswerEntity answerEntity = currentList.get(answerEntityList.getSkinOption());
                    answerEntityList.setSkinOption(answerEntityList.getSkinOption());
                } else {
                    entity.addSkinAnswerEntity(answerEntityList);
                }
            }

            // Xét nếu entity ko có trong request, xóa luôn
            for (SkinAnswerEntity answerEntityList : currentList.values()) {
                if(!request.getSkinAnswers().contains(answerEntityList.getSkinOption())){
                    entity.removeSkinAnswerEntity(answerEntityList);
                }
            }
        }

        // Vì phải set thủ công thằng con => đặt ở cuối
        mapper.updateSkinQuestionsEntity(entity, request);
        return mapper.toSkinQuestionsResponse(repository.save(entity));
    }

    public Map<String, SkinAnswerEntity> listAnswerstoMap(List<SkinAnswerEntity> skinAnswerEntityList){
        Map<String, SkinAnswerEntity> map = new HashMap<>();
        if(skinAnswerEntityList != null && !skinAnswerEntityList.isEmpty()){
            for (SkinAnswerEntity skinAnswerEntity : skinAnswerEntityList) {
                map.put(skinAnswerEntity.getSkinOption(), skinAnswerEntity);
            }
            return map;
        }
        return null;
    }

    public Status getStatus(String status){
        try {
            return Status.valueOf(status.toUpperCase());
        }catch (IllegalStateException e){
            log.error(e.getMessage());
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status.toUpperCase());
        List<SkinQuestionsEntity> entities = repository.findAllById(id);
        for(SkinQuestionsEntity entity : entities) {
            if (statusEnum.equals(Status.ACTIVE) || statusEnum.equals(Status.INACTIVE)) {
                entity.setStatus(statusEnum);
                repository.save(entity);
            }
            if (statusEnum.equals(Status.SOFT_DELETED)) {
                entity.setDeleted(true);
                repository.save(entity);
            }
            if (statusEnum.equals(Status.RESTORED)) {
                entity.setDeleted(false);
                repository.save(entity);
            }

            return "Cập nhật trạng thái câu hỏi thành công";
        }
        return "Cập nhật trạng thái câu hỏi thất bại";
    }

    @Override
    public boolean delete(Long aLong) {
        repository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.SKIN_QUESTIONS_NOT_FOUND));
        repository.deleteById(aLong);
        return true;
    }

    @Override
    public boolean delete(List<Long> longs) {
        repository.deleteAll(repository.findAllById(longs));
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
    public SkinQuestionsResponse showDetail(Long aLong) {
        SkinQuestionsEntity entity = repository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.SKIN_QUESTIONS_NOT_FOUND));
        return mapper.toSkinQuestionsResponse(entity);
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }


    public List<SkinQuestionsResponse> showAll(){
        return repository.findAll().stream().map(mapper::toSkinQuestionsResponse).collect(Collectors.toList());
    }

    public SkinQuestionsEntity getSkinQuestions(Long aLong) {
        return repository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.SKIN_QUESTIONS_NOT_FOUND));
    }
}
