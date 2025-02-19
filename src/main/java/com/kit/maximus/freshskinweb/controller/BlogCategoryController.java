package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.blog_category.BlogCategoryCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.BlogCategoryUpdateRequest;
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
        String message = "Create blog category successfully";
        var result = blogCategoryService.add(request);
        log.info("CREATE BLOG CATEGORY REQUEST");
        return ResponseAPI.<BlogCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("/edit/{id}")
    public ResponseAPI<BlogCategoryResponse> updateBlogCategory(@PathVariable Long id ,@RequestBody BlogCategoryUpdateRequest request){
        String message = "Update blog category successfully";
        BlogCategoryResponse result = blogCategoryService.update(id, request);
        log.info("UPDATE BLOG CATEGORY REQUEST");
        return ResponseAPI.<BlogCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteBlogCategory(@PathVariable Long id) {
        String message_succed = "Delete Blog Category successfull";
        String message_failed = "Delete Blog Category failed";
        boolean result = blogCategoryService.delete(id);
        if (result) {
            log.info(" Blog Category deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" Blog Category delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

//    @PatchMapping("deleteT/{id}")
//    public ResponseAPI<String> deleteTBlogCategory(@PathVariable Long id) {
//        String message_succed = "Delete Blog Category successfull";
//        String message_failed = "Delete Blog Category failed";
//        boolean result = blogCategoryService.deleteTemporarily(id);
//        if (result) {
//            log.info(" Blog Category deleted successfully!");
//            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
//        }
//        log.info(" Blog Category delete failed");
//        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
//    }

//    @PatchMapping("/restore/{id}")
//    public ResponseAPI<String> restoreBlogCategory(@PathVariable Long id) {
//        String message_succed = "Restore Blog Category successfull";
//        String message_failed = "Restore Blog Category failed";
//        var result = blogCategoryService.restore(id);
//        if (result) {
//            log.info(" Blog Category Restored successfully!");
//            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
//        }
//        log.info(" Blog Category Restored failed");
//        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
//    }

}
