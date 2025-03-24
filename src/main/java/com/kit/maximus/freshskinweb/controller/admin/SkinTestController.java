package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.service.skintest.SkinTestService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/skin/result")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkinTestController {

    SkinTestService skinTestService;

    @PostMapping("/create")
    public ResponseAPI<SkinTestResponse> create(@RequestBody SkinTestRequest request) {
        String message = "Tạo bài test da thành công";
        var result = skinTestService.add(request);
        if(!result){
            message = "Tạo bài test da thất bại";
            return ResponseAPI.<SkinTestResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .build();
        }
        log.info("CREATE QUESTION REQUEST SUCCESS");

        return ResponseAPI.<SkinTestResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<SkinTestResponse> getDetail(@PathVariable("id") Long id ) {
        var result = skinTestService.getDetail(id);
        return ResponseAPI.<SkinTestResponse>builder().data(result).build();
    }
}
