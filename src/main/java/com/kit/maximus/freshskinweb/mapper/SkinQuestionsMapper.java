package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.UpdateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinQuestionsResponse;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkinQuestionsMapper {

    SkinQuestionsEntity toSkinQuestionsEntity (CreateSkinQuestionsRequest request);

    SkinQuestionsResponse toSkinQuestionsResponse (SkinQuestionsEntity entity);

    void updateSkinQuestionsEntity (@MappingTarget SkinQuestionsEntity entity, UpdateSkinQuestionsRequest request);

}
