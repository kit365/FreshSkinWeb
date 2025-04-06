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
    @Mapping(target = "skinCareRountine", ignore = true)
    @Mapping(source = "content", target = "content")
    @Mapping(source = "step", target = "step")
    @Mapping(source = "position", target = "position")
    RountineStepEntity toRountineStepEntity(CreationRountineStepRequest request);

    @Mapping(target = "product",ignore = true)
    @Mapping(source = "content", target = "content")
    @Mapping(source = "step", target = "step")
    @Mapping(source = "position", target = "position")
    RountineStepResponse toRountineStepResponse(RountineStepEntity entity);

    @Mapping(target = "product",ignore = true)
    @Mapping(target = "skinCareRountine", ignore = true)
    @Mapping(source = "content", target = "content")
    @Mapping(source = "step", target = "step")
    @Mapping(source = "position", target = "position")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRountineStep(@MappingTarget RountineStepEntity entity, UpdationRountineStepRequest request);
}
