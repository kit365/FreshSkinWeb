package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.UpdationSkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkinCareRoutineMapper {
    @Mapping(target = "skinType", ignore = true)
    SkinCareRoutineEntity toEntity(SkinCareRountineRequest request);

    SkinCareRountineResponse toResponse(SkinCareRoutineEntity entity);

    List<SkinCareRountineResponse> toResponse(List<SkinCareRoutineEntity> entity);

    @Mapping(target = "skinType", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdationSkinCareRountineRequest request, @MappingTarget SkinCareRoutineEntity entity);
}
