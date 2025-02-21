package com.kit.maximus.freshskinweb.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.setting.CreateSettingRequest;
import com.kit.maximus.freshskinweb.dto.request.setting.UpdateSettingRequest;
import com.kit.maximus.freshskinweb.dto.response.SettingResponse;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import com.kit.maximus.freshskinweb.mapper.SettingMapper;
import com.kit.maximus.freshskinweb.repository.SettingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SettingService implements BaseService<SettingResponse, CreateSettingRequest, UpdateSettingRequest, Long>{

    SettingRepository settingRepository;
    SettingMapper settingMapper;

    public SettingResponse create(CreateSettingRequest request) {
        SettingEntity settingEntity = new SettingEntity();
        SettingEntity mapper = settingMapper.toSettingEntity(request);
        return settingMapper.toSetting(settingRepository.save(mapper));

    }



    @Override
    public boolean add(CreateSettingRequest request) {
        return false;
    }



    public SettingResponse update(Long id, UpdateSettingRequest request) {
        SettingEntity getSetting = settingRepository.findById(id).orElse(null);
        settingMapper.update(getSetting, request);

        return settingMapper.toSetting(settingRepository.save(getSetting));
    }

    public List<SettingResponse> getAll() {
        List<SettingEntity> setting = settingRepository.findAll();
        return settingMapper.toListSetting(setting);
    }

    @Override
    public String update(List<Long> id, String status) {
        return "";
    }

    @Override
    public boolean delete(Long id) {
        SettingEntity getSetting = settingRepository.findById(id).orElse(null);
        settingRepository.delete(getSetting);
        return true;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long aLong) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        return false;
    }

    @Override
    public SettingResponse showDetail(Long aLong) {
        return null;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }
}
