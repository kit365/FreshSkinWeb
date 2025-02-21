package com.kit.maximus.freshskinweb.controller;


import com.kit.maximus.freshskinweb.dto.request.setting.CreateSettingRequest;
import com.kit.maximus.freshskinweb.dto.request.setting.UpdateSettingRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SettingResponse;
import com.kit.maximus.freshskinweb.service.SettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/setting")
public class SettingController {
    SettingService settingService;


    @PostMapping("/create")
    public ResponseAPI<SettingResponse> create(@RequestBody CreateSettingRequest createSettingRequest) {
        String message = "Create Setting Success";
        SettingResponse settingResponse = settingService.create(createSettingRequest);
        return ResponseAPI.<SettingResponse>builder().code(HttpStatus.OK.value()).message(message).data(settingResponse).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<SettingResponse>> show() {
        String message = "Show Setting Success";
        List<SettingResponse> settingResponse = settingService.getAll();
        return ResponseAPI.<List<SettingResponse>>builder().code(HttpStatus.OK.value()).message(message).data(settingResponse).build();
    }

    @PatchMapping("/update/{id}")
    public ResponseAPI<SettingResponse> update(@PathVariable Long id, @RequestBody UpdateSettingRequest updateSettingRequest) {
        String message = "Update Setting Success";
        SettingResponse settingResponse = settingService.update(id, updateSettingRequest);
        return ResponseAPI.<SettingResponse>builder().code(HttpStatus.OK.value()).message(message).data(settingResponse).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<SettingResponse> delete(@PathVariable Long id) {
        String message = "Delete Setting Success";
        boolean settingResponse = settingService.delete(id);
        return ResponseAPI.<SettingResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

}
