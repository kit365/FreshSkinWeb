package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.blogCategory.BlogCategoryCreationRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.BlogCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/blogcategory")
public class BlogCategoryController {

    BlogCategoryService blogCategoryService;

    @PostMapping("/create")
    public ResponseAPI<BlogCategoryResponse> createBlogCategory(@RequestBody BlogCategoryCreationRequest request){
        System.out.println(request);
        String message = "Create blog category successfully";
        var result = blogCategoryService.add(request);
        log.info("CREATE BLOG CATEGORY REQUEST");
        return ResponseAPI.<BlogCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }
}
