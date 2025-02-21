package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.setting.CreateSettingRequest;
import com.kit.maximus.freshskinweb.dto.request.setting.UpdateSettingRequest;
import com.kit.maximus.freshskinweb.dto.response.SettingResponse;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SettingMapper {
    SettingResponse toSetting(SettingEntity request);
    SettingEntity toSettingEntity(SettingEntity request);
    SettingEntity toSettingEntity(CreateSettingRequest createSettingRequest);
    SettingResponse toSettingResponse(CreateSettingRequest createSettingRequest);

    SettingEntity updateSetting(@MappingTarget SettingEntity settingEntity, UpdateSettingRequest updateSettingRequest);
    void update(@MappingTarget SettingEntity settingEntity, UpdateSettingRequest updateSettingRequest);
    List<SettingResponse> toListSetting(List<SettingEntity> settingEntities);
}
