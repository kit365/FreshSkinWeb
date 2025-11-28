package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.question_group.CreationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.request.question_group.UpdationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.response.QuestionGroupResponse;
import com.kit.maximus.freshskinweb.entity.QuestionGroupEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {

    @Mapping(target = "questions", ignore = true)
    QuestionGroupEntity toEntity(CreationQuestionGroupRequest request);
    QuestionGroupResponse toResponse(QuestionGroupEntity questionGroupEntity);

    List<QuestionGroupResponse> toResponse(List<QuestionGroupEntity> questionGroupEntity);

    @Mapping(target = "questions", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget QuestionGroupEntity questionGroupEntity, UpdationQuestionGroupRequest request);
}
