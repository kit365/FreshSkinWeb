package com.kit.maximus.freshskinweb.mapper;


import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.UpdationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SkinTestMapper  {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    @Mapping(target = "questionGroup", ignore = true)
    SkinTestEntity toSkinTestEntity(CreationSkinTestRequest request);

    SkinTestResponse toSkinTestResponse(SkinTestEntity skinTestEntity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skinType", ignore = true)
    @Mapping(target = "questionGroup", ignore = true)
    @Mapping(target = "status", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSkinTestEntity(@MappingTarget SkinTestEntity skinTestEntity, UpdationSkinTestRequest request);


}
