package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.question_group.CreationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_answer.CreationSkinAnswerRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.response.QuestionGroupResponse;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.QuestionGroupMapper;
import com.kit.maximus.freshskinweb.repository.QuestionGroupRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        if (request.getTitle() == null || request.getDescription() == null) {
            throw new AppException(ErrorCode.QUESTION_GROUP_INVALID);
        }
        questionGroupEntity.setTitle(request.getTitle());
        questionGroupEntity.setDescription(request.getDescription());

        List<SkinQuestionsEntity> skinQuestionsEntities = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (CreateSkinQuestionsRequest createSkinQuestionsRequest : request.getQuestions()) {
                SkinQuestionsEntity skinQuestionsEntity = new SkinQuestionsEntity();
                skinQuestionsEntity.setQuestionText(createSkinQuestionsRequest.getQuestionText());

                List<SkinAnswerEntity> skinAnswers = new ArrayList<>();
                if (createSkinQuestionsRequest.getAnswers() != null) {
                    for (CreationSkinAnswerRequest creationSkinAnswerRequest : createSkinQuestionsRequest.getAnswers()) {
                        SkinAnswerEntity skinAnswerEntity = new SkinAnswerEntity();
                        skinAnswerEntity.setOption(creationSkinAnswerRequest.getOption());
                        skinAnswerEntity.setScore(creationSkinAnswerRequest.getScore());
                        skinAnswerEntity.setSkinQuestionsEntity(skinQuestionsEntity);
                        skinAnswers.add(skinAnswerEntity);
                    }
                }
                skinQuestionsEntity.setAnswers(skinAnswers);
                skinQuestionsEntity.setQuestionGroup(questionGroupEntity);
                skinQuestionsEntities.add(skinQuestionsEntity);
            }
        }
        questionGroupEntity.setQuestions(skinQuestionsEntities);

        questionGroupRepository.save(questionGroupEntity);

        return true;
    }

    public Page<QuestionGroupEntity> getPagedAndFilteredQuestionGroups(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<QuestionGroupEntity> spec = Specification.where(null);

        // Xử lý keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("description")), likePattern)
                );
            });
        }

        // Xử lý status
        if (status != null && !status.trim().isEmpty()) {
            try {
                Status statusEnum = Status.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("status"), statusEnum)
                );
            } catch (IllegalArgumentException e) {
                log.error("Invalid status value: {}", status);
                return Page.empty(pageable);
            }
        }

        try {
            return questionGroupRepository.findAll(spec, pageable);
        } catch (Exception e) {
            log.error("Error while fetching question groups: {}", e.getMessage());
            return Page.empty(pageable);
        }
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
            questionGroupEntity.setTitle(request.getTitle());

        } else {
            questionGroupEntity.setTitle(questionGroupEntity.getTitle());
        }

        if(request.getDescription() != null){
            questionGroupEntity.setDescription(request.getDescription());
        } else {
            questionGroupEntity.setDescription(questionGroupEntity.getDescription());
        }

        if(request.getStatus() != null){
            Status Status = getStatus(request.getStatus());
            questionGroupEntity.setStatus(Status);
        } else {
            questionGroupEntity.setStatus(questionGroupEntity.getStatus());
        }

        // Handle SkinQuestions update
        if (request.getQuestions() != null) {
            List<SkinQuestionsEntity> currentQuestions = questionGroupEntity.getQuestions();
            List<Long> requestQuestionIds = request.getQuestions().stream()
                    .map(CreateSkinQuestionsRequest::getId)
                    .filter(questionId -> questionId != null)
                    .collect(Collectors.toList());

            // Remove questions that are not in the request
            currentQuestions.removeIf(question -> !requestQuestionIds.contains(question.getId()));

            // Update existing questions and add new ones
            List<SkinQuestionsEntity> updatedQuestions = request.getQuestions().stream()
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
                        List<SkinAnswerEntity> currentAnswers = questionEntity.getAnswers();
                        if (currentAnswers == null) {
                            currentAnswers = new ArrayList<>();
                            questionEntity.setAnswers(currentAnswers);
                        } else {
                            currentAnswers.clear();
                        }

                        // Add new answers from request
                        if (questionRequest.getAnswers() != null) {
                            List<SkinAnswerEntity> newAnswers = questionRequest.getAnswers().stream()
                                    .map(answerRequest -> {
                                        SkinAnswerEntity answer = new SkinAnswerEntity();
                                        answer.setOption(answerRequest.getOption());
                                        answer.setScore(answerRequest.getScore());
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
            questionGroupEntity.getQuestions().clear();
        }

        // Save all changes
        questionGroupRepository.save(questionGroupEntity);
        return true;
    }

}
