package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.rountine_step.CreationRountineStepRequest;
import com.kit.maximus.freshskinweb.dto.request.rountine_step.UpdationRountineStepRequest;
import com.kit.maximus.freshskinweb.dto.response.RountineStepResponse;
import com.kit.maximus.freshskinweb.entity.RountineStepEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RountineStepMapper{

    @Mapping(target = "product",ignore = true)
    @Mapping(target = "skinCareRoutine", ignore = true)
    RountineStepEntity toRountineStepEntity(CreationRountineStepRequest request);

    @Mapping(target = "product",ignore = true)
    RountineStepResponse toRountineStepResponse(RountineStepEntity entity);

    @Mapping(target = "product",ignore = true)
    List<RountineStepResponse> toRountinesStepResponse(List<RountineStepEntity> entity);

    @Mapping(target = "product",ignore = true)
    @Mapping(target = "skinCareRoutine", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRountineStep(@MappingTarget RountineStepEntity entity, UpdationRountineStepRequest request);
}
