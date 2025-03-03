package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.UpdateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinQuestionsResponse;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.service.SkinTestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/skintest")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkinTestController {
    SkinTestService skinTestService;

    @PostMapping("/create")
    public ResponseAPI<SkinTestResponse> create(@RequestBody CreationSkinTestRequest request) {
        var result = skinTestService.add(request);
        log.info("CREATE QUESTION REQUEST SUCCESS");

        return ResponseAPI.<SkinTestResponse>builder()
                .code(HttpStatus.OK.value())
                .build();
    }
    
}
