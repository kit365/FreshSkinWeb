package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.question_group.CreationQuestionGroupRequest;
import com.kit.maximus.freshskinweb.dto.response.QuestionGroupResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.entity.QuestionGroupEntity;
import com.kit.maximus.freshskinweb.service.skintest.QuestionGroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/question/group")
public class QuestionGroupController {
    QuestionGroupService questionGroupService;

    @PostMapping("/create")
    public ResponseAPI<Boolean> add(@RequestBody CreationQuestionGroupRequest request){
        log.info("Add question group: " + request);
        String message = "thêm bộ câu hỏi thành công ";
        var result = questionGroupService.add(request);

        if(!result){
            message = "thêm bộ câu hỏi thất bại ";
        }
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(true)
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<Map<String, Object>> getPagedAndFilteredQuestionGroups(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QuestionGroupEntity> result = questionGroupService.getPagedAndFilteredQuestionGroups(keyword, status, page, size);

        Map<String, Object> response = Map.of(
                "QuestionGroup", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "size", result.getSize(),
                "number", result.getNumber()
        );

        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<QuestionGroupResponse> showByID(@PathVariable Long id){
        log.info("Show question group with id: " + id);
        QuestionGroupResponse result = questionGroupService.getQuestionGroupById(id);
        return ResponseAPI.<QuestionGroupResponse>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<Boolean> delete(@PathVariable Long id) {
        log.info("Delete question group with id: " + id);
        String message = "Xóa bộ câu hỏi thành công";
        boolean result = questionGroupService.delete(id);

        if (!result) {
            message = "Xóa bộ câu hỏi thất bại";
        }
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

    @PatchMapping("/edit/{id}")
    public ResponseAPI<Boolean> update(@PathVariable Long id, @RequestBody CreationQuestionGroupRequest request) {
        log.info("Update question group with id: " + id);
        String message = "Cập nhật bộ câu hỏi thành công";
        boolean result = questionGroupService.update(id, request);

        if (!result) {
            message = "Cập nhật bộ câu hỏi thất bại";
        }
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

}
