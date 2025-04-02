package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.UpdationSkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;

import com.kit.maximus.freshskinweb.service.skintest.SkinCareRountineService;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/skin-care-routines")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SkinCareRountineController {

    SkinCareRountineService skinCareRountineService;


    @PostMapping("create")
    public ResponseAPI<Boolean> addSkinCareRoutine(@RequestBody SkinCareRountineRequest request) {
        boolean result = skinCareRountineService.add(request);
        String message ="Thêm lộ trình da thành công";
        if(!result){
            message = "Thêm lộ trình da thất bại";
        }
        return ResponseAPI .<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<Boolean> updateSkinCareRoutine(@PathVariable Long id, @RequestBody UpdationSkinCareRountineRequest request) {
        boolean response = skinCareRountineService.update(id, request);
        String message = "Cập nhật lộ trình da thành công";
        if(!response){
            message = "Cập nhật lộ trình da thất bại";
            return ResponseAPI.<Boolean>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .build();
        }
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<Boolean> deleteSkinCareRoutine(@PathVariable Long id) {
        boolean result = skinCareRountineService.delete(id);
        String message ="Xóa lộ trình da thành công";
        if(!result){
            message = "Xóa lộ trình da thất bại";
        }
        return ResponseAPI .<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkinCareRountineResponse> getSkinCareRoutine(@PathVariable Long id) {
        SkinCareRountineResponse response = skinCareRountineService.get(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseAPI<Page<SkinCareRountineResponse>> getFilteredSkinCareRoutines(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseAPI.<Page<SkinCareRountineResponse>>builder()

                .data(skinCareRountineService.getFilteredSkinCareRoutines(status, keyword, PageRequest.of(page, size)))
                .build();

    }

}
