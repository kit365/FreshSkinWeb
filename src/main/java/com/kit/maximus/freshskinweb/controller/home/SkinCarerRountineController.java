package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRoutineResponseDTO;
import com.kit.maximus.freshskinweb.service.skintest.SkinCareRountineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/home/skincare/rountine")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkinCarerRountineController {

    SkinCareRountineService skinCareRountineService;

    @PostMapping()
    public ResponseAPI<SkinCareRoutineResponseDTO> getRoutineAndProducts(
            @RequestBody Map<String, Object> skinTypeRequestDTO,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "7") Integer size) {

        String skinType = skinTypeRequestDTO.get("skinType").toString();

        SkinCareRoutineResponseDTO result = skinCareRountineService.getRoutineAndProducts(skinType, page, size);
        return ResponseAPI.<SkinCareRoutineResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }
}
