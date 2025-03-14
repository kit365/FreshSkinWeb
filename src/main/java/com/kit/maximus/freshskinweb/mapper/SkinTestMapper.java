package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SkinTestMapper  {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "questionGroup", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "totalScore", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    SkinTestEntity toSkinTestEntity(SkinTestRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "questionGroup", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "totalScore", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    SkinTestResponse toSkinTestResponse(SkinTestEntity skinTestEntity);

}
