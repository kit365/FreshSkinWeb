package com.kit.maximus.freshskinweb.controller;


import com.kit.maximus.freshskinweb.dto.request.setting.SettingRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SettingResponse;
import com.kit.maximus.freshskinweb.service.SettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/setting")
public class SettingController {
    SettingService settingService;


    @GetMapping("/show")
    public ResponseAPI<List<SettingResponse>> show() {
        String message = "Show Setting Success";
        List<SettingResponse> settingResponse = settingService.getAll();
        return ResponseAPI.<List<SettingResponse>>builder().code(HttpStatus.OK.value()).message(message).data(settingResponse).build();
    }

    @PatchMapping("/edit/{id}")
    public ResponseAPI<SettingResponse> update(@PathVariable("id") String id, @RequestBody SettingRequestDTO settingRequestDTO) {
        if(id.equalsIgnoreCase("null")) id = "0";
        String message = "Update Setting Success";
        SettingResponse settingResponse = settingService.update(id, settingRequestDTO);
        return ResponseAPI.<SettingResponse>builder().code(HttpStatus.OK.value()).message(message).data(settingResponse).build();
    }


}
