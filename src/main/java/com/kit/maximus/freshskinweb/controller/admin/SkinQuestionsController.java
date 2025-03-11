package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_questions.UpdateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.SkinQuestionsResponse;
import com.kit.maximus.freshskinweb.service.SkinQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/skinquestions")
//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SkinQuestionsController {
    SkinQuestionService skinQuestionService;

    @PostMapping("/create")
    public ResponseAPI<SkinQuestionsResponse> create(@RequestBody CreateSkinQuestionsRequest request) {
        String message_succed = "Tạo câu hỏi thành công";
        var result = skinQuestionService.add(request);
        log.info("CREATE QUESTION REQUEST SUCCESS");

        return ResponseAPI.<SkinQuestionsResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message_succed)
                .build();
    }

    @PatchMapping(value = "edit/{id}")
    public ResponseAPI<SkinQuestionsResponse> update(@PathVariable Long id, @RequestBody UpdateSkinQuestionsRequest request) {
        String message_succed = "Cập nhật câu hỏi thành công";
        var result = skinQuestionService.update(id, request);
        log.info("CREATE QUESTION REQUEST SUCCESS");

        return ResponseAPI.<SkinQuestionsResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message_succed)
                .data(result)
                .build();
    }

    @GetMapping
    public ResponseAPI<List<SkinQuestionsResponse>> showAll(){
        var result = skinQuestionService.showAll();
        return ResponseAPI.<List<SkinQuestionsResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<SkinQuestionsResponse> showDetail(@PathVariable("id") Long id){
        var result = skinQuestionService.showDetail(id);
        return ResponseAPI.<SkinQuestionsResponse>builder().data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<SkinQuestionsResponse> deleteRole(@PathVariable("id") Long id){
        String message = "Delete question successfully";
        skinQuestionService.delete(id);
        log.info(message);
        return ResponseAPI.<SkinQuestionsResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @DeleteMapping("/delete")
    public ResponseAPI<SkinQuestionsResponse> deleteRole(@RequestBody Map<String, Object> questions){
        String message = "Delete selected questions successfully";
        List<Long> ids = (List<Long>) questions.get("id");
        skinQuestionService.delete(ids);
        log.info(message);
        return ResponseAPI.<SkinQuestionsResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }


}
