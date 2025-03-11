package com.kit.maximus.freshskinweb.controller.home;



import com.kit.maximus.freshskinweb.dto.request.search_keyword.KeywordRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.service.SearchKeywordService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("home/hotkeyword")
@RequiredArgsConstructor
public class SearchKeywordController {

    SearchKeywordService searchKeywordService;

    @PostMapping("/create")
    public ResponseAPI addKeyword(@RequestBody KeywordRequest request){
        String keyword = request.getKeyword();
        var result = searchKeywordService.addKeyword(keyword);
        String message = "Create keyword succsessly";
        if(!result) {
            message = "Create keyword failed";
        }
        return ResponseAPI.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @GetMapping
    public ResponseAPI<Map<String, Object>> showKeywords() {
        List<KeywordResponse> keywords = searchKeywordService.showKeyword();

        // Tạo map để chuẩn hóa dữ liệu trả về
        Map<String, Object> response = new HashMap<>();
        response.put("keywords", keywords);
        response.put("totalKeywords", keywords.size());

        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách từ khóa thành công")
                .data(response)
                .build();
    }
}
