package com.kit.maximus.freshskinweb.service.home;


import com.kit.maximus.freshskinweb.dto.request.setting.SettingRequestDTO;
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

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SettingService {

    SettingRepository settingRepository;
    SettingMapper settingMapper;



    public SettingResponse update(String id, SettingRequestDTO request) {
        Long ID = Long.parseLong(id);
        SettingEntity getSetting = settingRepository.findById(ID).orElse(null);
        if(getSetting == null ) {
            getSetting = new SettingEntity();
            getSetting.setWebsiteName("");
            getSetting.setLogo("");
            getSetting.setPhone("");
            getSetting.setEmail("");
            getSetting.setAddress("");
            getSetting.setCopyright("");
            getSetting.setFacebook("");
            getSetting.setTwitter("");
            getSetting.setYoutube("");
            getSetting.setInstagram("");
            getSetting.setPolicy1("");
            getSetting.setPolicy2("");
            getSetting.setPolicy3("");
            getSetting.setPolicy4("");
            getSetting.setPolicy5("");
            getSetting.setPolicy6("");
            getSetting.setSupport1("");
            getSetting.setSupport2("");
            getSetting.setSupport3("");
            getSetting.setSupport4("");
            getSetting.setSupport5("");
            getSetting.setSupport6("");
            return settingMapper.toSetting(settingRepository.save(getSetting));
        }
        settingMapper.update(getSetting, request);
        return settingMapper.toSetting(settingRepository.save(getSetting));
    }

    public List<SettingResponse> getAll() {
        List<SettingEntity> setting = settingRepository.findAll();


        return settingMapper.toListSetting(setting);
    }


}
