package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.UpdationSkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;

import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import com.kit.maximus.freshskinweb.service.skintest.SkinCareRountineService;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/skin-care-routines")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
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

    @GetMapping("/{id}")
    public ResponseAPI<SkinCareRountineResponse> getSkinCareRountine(@PathVariable("id") Long skinTypeId) {
        SkinCareRountineResponse skinCareRoutine = skinCareRountineService.getById(skinTypeId);
        return ResponseAPI.<SkinCareRountineResponse>builder()
                .code(200)
                .message("Lấy lộ trình da thành công")
                .data(skinCareRoutine)
                .build();
    }

    @GetMapping
    public ResponseAPI<Page<SkinCareRountineResponse>> getAllSkinCareRountine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
            ){

        Page<SkinCareRountineResponse> skinCareRoutines = skinCareRountineService.getAllSkinCareRoutines(page, size);
        return ResponseAPI.<Page<SkinCareRountineResponse>>builder()
                .code(200)
                .message("Lấy lộ trình da thành công")
                .data(skinCareRoutines)
                .build();
    }
    @PatchMapping("update/{id}")
    public ResponseAPI<Boolean> updateSkinCareRoutine(@PathVariable Long id, @RequestBody UpdationSkinCareRountineRequest request) {

            boolean result = skinCareRountineService.update(id, request);

            if (!result) {
                return ResponseAPI.<Boolean>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Cập nhật lộ trình da thất bại")
                        .build();
            }

            return ResponseAPI.<Boolean>builder()
                    .code(HttpStatus.OK.value())
                    .message("Cập nhật lộ trình da thành công")
                    .data(result)
                    .build();

    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<Boolean> deleteSkinCareRoutine(@PathVariable Long id) {

            boolean result = skinCareRountineService.delete(id);

            if (!result) {
                return ResponseAPI.<Boolean>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Xóa lộ trình da thất bại")
                        .build();
            }

            return ResponseAPI.<Boolean>builder()
                    .code(HttpStatus.OK.value())
                    .message("Xóa lộ trình da thành công")
                    .data(result)
                    .build();

        }

}
