package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.UpdateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinQuestionsResponse;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import org.mapstruct.*;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface SkinQuestionsMapper {

    @Mapping( target=  "answers", ignore = true)
    @Mapping( target=  "questionGroup", ignore = true)
    SkinQuestionsEntity toSkinQuestionsEntity (CreateSkinQuestionsRequest request);

    SkinQuestionsResponse toSkinQuestionsResponse (SkinQuestionsEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping( target=  "answers", ignore = true)
    @Mapping( target=  "questionGroup", ignore = true)
    void updateSkinQuestionsEntity (@MappingTarget SkinQuestionsEntity entity, UpdateSkinQuestionsRequest request);

}
