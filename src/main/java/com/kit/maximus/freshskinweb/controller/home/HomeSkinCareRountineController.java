package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.service.skintest.SkinCareRountineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/home/skincare/rountine")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeSkinCareRountineController {

    SkinCareRountineService skinCareRountineService;

    @PostMapping()
    public ResponseAPI<SkinCareRountineResponse> getRountine(@RequestBody Map<String, Object> skinType) {
        String skinTypeValue = (String) skinType.get("skinType");
        if (skinType == null || skinType.isEmpty()) {
            return ResponseAPI.<SkinCareRountineResponse>builder()
                    .code(400)
                    .message("Vui lòng cung cấp loại da")
                    .build();
        }
    SkinCareRountineResponse skinCareRoutine = skinCareRountineService.getBySkinType(skinTypeValue);
        return ResponseAPI.<SkinCareRountineResponse>builder()
                .code(200)
                .message("Lấy lộ trình da thành công")
                .data(skinCareRoutine)
                .build();
    }
}
