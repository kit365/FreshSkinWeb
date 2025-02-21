package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.setting.SettingRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.SettingResponse;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SettingMapper {
    SettingResponse toSetting(SettingEntity request);

    SettingEntity updateSetting(@MappingTarget SettingEntity settingEntity, SettingRequestDTO settingRequestDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget SettingEntity settingEntity, SettingRequestDTO settingRequestDTO);
    List<SettingResponse> toListSetting(List<SettingEntity> settingEntities);
}
