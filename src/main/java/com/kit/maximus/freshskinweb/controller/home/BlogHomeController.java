package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.BlogCategoryService;
import com.kit.maximus.freshskinweb.service.BlogService;
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
    BlogService blogService;

    @GetMapping("")
    public ResponseAPI<Map<String, Object>> getBlogCategory(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "8") int size,
                                                            @RequestParam(value = "id", required = false) Long cateID) {
        String message_succed = "Thành công!!!";
        String message_failed = "Thất bại!!!";
        try {
            Map<String, Object> result = blogService.getBlogCategories(page,size,cateID);
            return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).message(message_failed).build();
        }
    }

    @GetMapping("{slug}")
    public ResponseAPI<BlogResponse> getBlogDetail(@PathVariable("slug") String slug) {
        String message_succed = "Thành công!!!";
        String message_failed = "Thất bại!!!";
        try {
            var result = blogService.getBlogResponseBySlug(slug);
            return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).data(result).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).message(message_failed).build();
        }
    }


}
