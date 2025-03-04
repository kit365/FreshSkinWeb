package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.UpdationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type.UpdateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeResponse;
import com.kit.maximus.freshskinweb.service.SkinTestService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        String message = "Tạo bài test da thành công";
        var result = skinTestService.add(request);
        log.info("CREATE QUESTION REQUEST SUCCESS");

        return ResponseAPI.<SkinTestResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<SkinTestResponse> update(@PathVariable("id") Long id, @Valid @RequestBody UpdationSkinTestRequest request) {
        String message = "Update skin type successfully";
        var result = skinTestService.update(id, request);
        return ResponseAPI.<SkinTestResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<SkinTestResponse>> showAll() {
        String message = "Get all skin type successfully";
        var result = skinTestService.showAll();
        return ResponseAPI.<List<SkinTestResponse>>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<String> delete(@PathVariable("id") Long id) {
        String message = "Delete skin type successfully";
        skinTestService.delete(id);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<SkinTestResponse> update(@PathVariable("id") Long id ) {
        var result = skinTestService.showDetail(id);
        return ResponseAPI.<SkinTestResponse>builder().data(result).build();
    }
}
