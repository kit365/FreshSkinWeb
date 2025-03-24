package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.CreationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type_score_range.UpdationSkinTypeScoreRangeRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeScoreRangeResponse;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.skintest.SkinTypeScoreRangeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/skintypes/score-range")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SkinTypeScoreRangeController {

    SkinTypeScoreRangeService service;

    @PostMapping("create")
    public ResponseAPI<Boolean> addSkinTypeScoreRange(@RequestBody CreationSkinTypeScoreRangeRequest entity) {
        String message_succed = "Tạo mức điểm cho loại da thành công";
        String message_failed = "Tạo mức điểm cho loại da thất bại";
        var result = service.add(entity);
        if(result){
            return ResponseAPI.<Boolean>builder().message(message_succed)
                    .code(HttpStatus.OK.value())
                    .message(message_succed)
                    .data(result)
                    .build();
        } else {
            return ResponseAPI.<Boolean>builder().message(message_succed)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .data(result)
                    .build();
        }
    }

    @GetMapping
    public ResponseAPI<Map<String, Object>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        var result = service.getAll(status, keyword, page, size, sortDir);
        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<Boolean> update(@PathVariable Long id, @RequestBody UpdationSkinTypeScoreRangeRequest request) {
        var result = service.update(id, request);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật mức điểm loại da thành công")
                .data(result)
                .build();
    }

    @GetMapping("{id}")
    public ResponseAPI<SkinTypeScoreRangeResponse> getDetail(@PathVariable Long id) {
        var result = service.getDetail(id);
        return ResponseAPI.<SkinTypeScoreRangeResponse>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @PatchMapping("/change-multi")
    public ResponseAPI<String> updateStatus(
            @RequestBody Map<String, Object> map ) {

        if (!map.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_SCORE_RANGE_ID);
        }

        List<Long> ids = (List<Long>) map.get("id");
        String status = map.get("status").toString();

        var result = service.update(ids, status);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<Boolean> delete(@PathVariable("id") Long id) {

        String message = "Xóa mức điểm loại da thành công";

        var result = service.delete(id);

        if(!result){
            message = "Xóa mức điểm loại da thất bại";
        }

        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<Boolean> deleteSelected(@RequestBody Map<String, Object>  map) {

        if (!map.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_SCORE_RANGE_ID);
        }

        List<Long> ids = (List<Long>) map.get("id");

        var result = service.deleteSelectedPart(ids);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa các mức điểm loại da đã chọn thành công")
                .data(result)
                .build();
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<Boolean> deleteTemporarily(@PathVariable Long id) {
        var result = service.deleteTemporarily(id);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa tạm thời mức điểm loại da thành công")
                .data(result)
                .build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<Boolean> restore(@PathVariable Long id) {
        var result = service.restore(id);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Khôi phục mức điểm loại da thành công")
                .data(result)
                .build();
    }
}
