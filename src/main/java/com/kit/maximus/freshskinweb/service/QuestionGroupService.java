package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.question_group.CreationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_answer.CreationSkinAnswerRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.entity.QuestionGroupEntity;
import com.kit.maximus.freshskinweb.entity.SkinAnswerEntity;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.QuestionGroupMapper;
import com.kit.maximus.freshskinweb.repository.QuestionGroupRepository;
import com.kit.maximus.freshskinweb.specification.QuestionGroupSpecification;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionGroupService {

    QuestionGroupRepository questionGroupRepository;
    QuestionGroupMapper questionGroupMapper;

    public boolean add(CreationQuestionGroupRequest request) {
        QuestionGroupEntity questionGroupEntity = new QuestionGroupEntity();
        questionGroupEntity.setGroupName(request.getGroupName());
        questionGroupEntity.setDescription(request.getDescription());

        List<SkinQuestionsEntity> skinQuestionsEntities = new ArrayList<>();
        if (request.getSkinQuestionsEntities() != null) {
            for (CreateSkinQuestionsRequest createSkinQuestionsRequest : request.getSkinQuestionsEntities()) {
                SkinQuestionsEntity skinQuestionsEntity = new SkinQuestionsEntity();
                skinQuestionsEntity.setQuestionText(createSkinQuestionsRequest.getQuestionText());

                List<SkinAnswerEntity> skinAnswers = new ArrayList<>();
                if (createSkinQuestionsRequest.getSkinAnswers() != null) {
                    for (CreationSkinAnswerRequest creationSkinAnswerRequest : createSkinQuestionsRequest.getSkinAnswers()) {
                        SkinAnswerEntity skinAnswerEntity = new SkinAnswerEntity();
                        skinAnswerEntity.setSkinOption(creationSkinAnswerRequest.getSkinOption());
                        skinAnswerEntity.setAnswerScore(creationSkinAnswerRequest.getAnswerScore());
                        skinAnswerEntity.setSkinQuestionsEntity(skinQuestionsEntity);
                        skinAnswers.add(skinAnswerEntity);
                    }
                }
                skinQuestionsEntity.setSkinAnswers(skinAnswers);
                skinQuestionsEntity.setQuestionGroup(questionGroupEntity);
                skinQuestionsEntities.add(skinQuestionsEntity);
            }
        }
        questionGroupEntity.setSkinQuestionsEntities(skinQuestionsEntities);

        questionGroupRepository.save(questionGroupEntity);

        return true;
    }

    public Page<QuestionGroupEntity> getPagedAndFilteredQuestionGroups(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<QuestionGroupEntity> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and(QuestionGroupSpecification.hasGroupName(keyword));
        }
        if (status != null && !status.isEmpty()) {
            Status statusEnum = getStatus(status);
            spec = spec.and(QuestionGroupSpecification.hasStatus(statusEnum));
        }

        Page<QuestionGroupEntity> result = questionGroupRepository.findAll(spec, pageable);

        if (keyword != null && !keyword.isEmpty() && result.isEmpty()) {
            return Page.empty(pageable);
        }

        return result;
    }

    public Status getStatus(String status){
        if (status != null){
            return Status.valueOf(status.toUpperCase());
        } else {
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }
}
