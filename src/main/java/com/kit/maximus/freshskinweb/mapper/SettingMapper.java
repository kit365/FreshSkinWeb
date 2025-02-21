package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.setting.SettingRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.SettingResponse;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SettingMapper {
    SettingResponse toSetting(SettingEntity request);

    SettingEntity updateSetting(@MappingTarget SettingEntity settingEntity, SettingRequestDTO settingRequestDTO);
    void update(@MappingTarget SettingEntity settingEntity, SettingRequestDTO settingRequestDTO);
    List<SettingResponse> toListSetting(List<SettingEntity> settingEntities);
}
