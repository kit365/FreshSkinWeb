package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.BlogCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/blogs")
public class BlogHomeController {

    BlogCategoryService blogCategoryService;

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getBlogCategory(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "8") int size) {
        String message_succed = "Thành công!!!";
        String message_failed = "Thất bại!!!";
        try {
            Map<String, Object> result = blogCategoryService.getBlogCategories(page,size);
            return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).message(message_failed).build();
        }


    }


}
