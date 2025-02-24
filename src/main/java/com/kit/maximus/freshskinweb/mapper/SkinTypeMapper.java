package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.skin_type.CreateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type.UpdateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeResponse;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SkinTypeMapper {

    SkinTypeEntity toSkinTypeEntity(CreateSkinTypeRequest request);

    SkinTypeResponse toSkinTypeResponse(SkinTypeEntity request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSkinType(@MappingTarget SkinTypeEntity skinTypeEntity, UpdateSkinTypeRequest request);
}
