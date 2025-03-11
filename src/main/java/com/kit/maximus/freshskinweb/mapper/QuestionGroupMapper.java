package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.question_group.CreationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.request.question_group.UpdationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.response.QuestionGroupResponse;
import com.kit.maximus.freshskinweb.entity.QuestionGroupEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {

    @Mapping(target = "skinQuestionsEntities", ignore = true)
    QuestionGroupEntity toEntity(CreationQuestionGroupRequest request);

    QuestionGroupResponse toResponse(QuestionGroupEntity questionGroupEntity);

    @Mapping(target = "skinQuestionsEntities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget QuestionGroupEntity questionGroupEntity, UpdationQuestionGroupRequest request);
}
