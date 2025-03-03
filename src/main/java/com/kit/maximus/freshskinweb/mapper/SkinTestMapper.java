package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.UpdationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkinTestMapper  {
    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    SkinTestEntity toSkinTestEntity(CreationSkinTestRequest request);

    SkinTestResponse toSkinTestResponse(SkinTestEntity skinTestEntity);

    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    void updateSkinTestEntity(@MappingTarget SkinTestEntity skinTestEntity, UpdationSkinTestRequest request);


}
