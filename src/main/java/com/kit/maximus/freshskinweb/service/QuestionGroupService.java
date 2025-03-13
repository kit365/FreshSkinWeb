package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.question_group.CreationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_answer.CreationSkinAnswerRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.QuestionGroupResponse;
import com.kit.maximus.freshskinweb.dto.response.SkinQuestionsResponse;
import com.kit.maximus.freshskinweb.entity.*;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionGroupService {

    QuestionGroupRepository questionGroupRepository;
    QuestionGroupMapper questionGroupMapper;

    public boolean add(CreationQuestionGroupRequest request) {
        QuestionGroupEntity questionGroupEntity = new QuestionGroupEntity();
        if (request.getGroupName() == null || request.getDescription() == null) {
            throw new AppException(ErrorCode.QUESTION_GROUP_INVALID);
        }
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

    public Status getStatus(String status) {
        if (status != null) {
            return Status.valueOf(status.toUpperCase());
        } else {
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    public QuestionGroupResponse getQuestionGroupById(Long id) {
        return questionGroupRepository.findById(id)
                .map(questionGroupMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND));
    }

    public boolean delete(Long id) {
        QuestionGroupEntity questionGroupEntity = questionGroupRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND));
        questionGroupRepository.delete(questionGroupEntity);
        return true;
    }

    public boolean update(Long id, CreationQuestionGroupRequest request) {
        // Find the current QuestionGroup
        QuestionGroupEntity questionGroupEntity = questionGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND));

        // Update basic QuestionGroup fields
        if(request.getDescription() != null){
            questionGroupEntity.setGroupName(request.getGroupName());

        } else {
            questionGroupEntity.setGroupName(questionGroupEntity.getGroupName());
        }

        if(request.getDescription() != null){
            questionGroupEntity.setDescription(request.getDescription());
        } else {
            questionGroupEntity.setDescription(questionGroupEntity.getDescription());
        }

        // Handle SkinQuestions update
        if (request.getSkinQuestionsEntities() != null) {
            List<SkinQuestionsEntity> currentQuestions = questionGroupEntity.getSkinQuestionsEntities();
            List<Long> requestQuestionIds = request.getSkinQuestionsEntities().stream()
                    .map(CreateSkinQuestionsRequest::getId)
                    .filter(questionId -> questionId != null)
                    .collect(Collectors.toList());

            // Remove questions that are not in the request
            currentQuestions.removeIf(question -> !requestQuestionIds.contains(question.getId()));

            // Update existing questions and add new ones
            List<SkinQuestionsEntity> updatedQuestions = request.getSkinQuestionsEntities().stream()
                    .map(questionRequest -> {
                        SkinQuestionsEntity questionEntity;

                        if (questionRequest.getId() != null) {
                            // Update existing question
                            questionEntity = currentQuestions.stream()
                                    .filter(q -> q.getId().equals(questionRequest.getId()))
                                    .findFirst()
                                    .orElseGet(SkinQuestionsEntity::new);
                        } else {
                            // Create new question
                            questionEntity = new SkinQuestionsEntity();
                        }

                        // Update question fields
                        questionEntity.setQuestionText(questionRequest.getQuestionText());
                        questionEntity.setQuestionGroup(questionGroupEntity);

                        // Handle answers
                        List<SkinAnswerEntity> currentAnswers = questionEntity.getSkinAnswers();
                        if (currentAnswers == null) {
                            currentAnswers = new ArrayList<>();
                            questionEntity.setSkinAnswers(currentAnswers);
                        } else {
                            currentAnswers.clear();
                        }

                        // Add new answers from request
                        if (questionRequest.getSkinAnswers() != null) {
                            List<SkinAnswerEntity> newAnswers = questionRequest.getSkinAnswers().stream()
                                    .map(answerRequest -> {
                                        SkinAnswerEntity answer = new SkinAnswerEntity();
                                        answer.setSkinOption(answerRequest.getSkinOption());
                                        answer.setAnswerScore(answerRequest.getAnswerScore());
                                        answer.setSkinQuestionsEntity(questionEntity);
                                        return answer;
                                    })
                                    .collect(Collectors.toList());
                            currentAnswers.addAll(newAnswers);
                        }

                        return questionEntity;
                    })
                    .collect(Collectors.toList());

            // Clear and set new questions
            currentQuestions.clear();
            currentQuestions.addAll(updatedQuestions);
        } else {
            // If no questions in request, clear all existing questions
            questionGroupEntity.getSkinQuestionsEntities().clear();
        }

        // Save all changes
        questionGroupRepository.save(questionGroupEntity);
        return true;
    }

}
