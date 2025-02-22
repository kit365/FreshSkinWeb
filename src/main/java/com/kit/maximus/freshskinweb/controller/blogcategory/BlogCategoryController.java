package com.kit.maximus.freshskinweb.controller.blogcategory;

import com.kit.maximus.freshskinweb.dto.request.blog_category.CreateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.BlogCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/blogs/category")
public class BlogCategoryController {

    BlogCategoryService blogCategoryService;

    @PostMapping("/create")
    public ResponseAPI<BlogCategoryResponse> createBlogCategory(@RequestBody CreateBlogCategoryRequest request){
        String message = "Create blog category successfully";
        var result = blogCategoryService.add(request);
        log.info("CREATE BLOG CATEGORY REQUEST");
        return ResponseAPI.<BlogCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/edit/{id}")
    public ResponseAPI<BlogCategoryResponse> updateBlogCategory(@PathVariable Long id ,@RequestBody UpdateBlogCategoryRequest request){
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

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<String> deleteTBlogCategory(@PathVariable Long id) {
        String message_succed = "Delete Blog Category successfull";
        String message_failed = "Delete Blog Category failed";
        boolean result = blogCategoryService.deleteTemporarily(id);
        if (result) {
            log.info(" Blog Category deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" Blog Category delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<String> restoreBlogCategory(@PathVariable Long id) {
        String message_succed = "Restore BlogCategory successfull";
        String message_failed = "Restore BlogCategory failed";
        var result = blogCategoryService.restore(id);
        if (result) {
            log.info(" BlogCategory Restored successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" BlogCategory Restored failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }
/// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// MY CODE HERE =>
    @PatchMapping("change-multi")
    public ResponseAPI<String> updataBlogCategory(@RequestBody Map<String,Object> requestBlogCategory) {

        if(!requestBlogCategory.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            //sua lai thong bao loi
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids =  (List<Long>) requestBlogCategory.get("id");
        String status  =  requestBlogCategory.get("status").toString();

        var result = blogCategoryService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBlogCategory(@RequestBody Map<String,Object> request) {

        if(!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "delete BlogCategory successfull";
        String message_failed = "delete BlogCategory failed";
        var result = blogCategoryService.delete(ids);
        if (result) {
            log.info("BlogCategory delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("BlogCategory delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<BlogCategoryResponse>> showBlogCategory() {
        return  ResponseAPI.<List<BlogCategoryResponse>>builder().code(HttpStatus.OK.value()).data(blogCategoryService.getAll()).build();
    }

/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



}
