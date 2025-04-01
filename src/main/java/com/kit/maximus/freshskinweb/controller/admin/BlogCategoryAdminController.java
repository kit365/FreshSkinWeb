package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.CreateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.blog.BlogCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/blogs/category")
public class BlogCategoryAdminController {

    BlogCategoryService blogCategoryService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductResponseDTO> createProduct(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnail", required = false) List<MultipartFile> images) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", images);
        String message_succed = "Tạo danh mục bài viết thành công";
        String message_failed = "Tạo danh mục bài viết thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateBlogCategoryRequest blogCategoryRequest = objectMapper.readValue(requestJson, CreateBlogCategoryRequest.class);
            blogCategoryRequest.setImage(images);

            var result = blogCategoryService.add(blogCategoryRequest);

            log.info("CREATE BLOG-CATEGORY REQUEST SUCCESS");
            return ResponseAPI.<ProductResponseDTO>builder()
                    .code(HttpStatus.OK.value())
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("CREATE BLOG-CATEGORY ERROR: " + e.getMessage());
            return ResponseAPI.<ProductResponseDTO>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }


    @PatchMapping("update/{id}")
    public ResponseAPI<String> updateBlogCategory(@PathVariable("id") int id,
                                                  @RequestBody Map<String, Object> request) {

        String statusEdit = (String) request.get("statusEdit");
        String status = (String) request.get("status");
        int position = request.containsKey("position") ? (int) request.get("position") : 0;

        String result = blogCategoryService.update(id, status, position, statusEdit);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }


    @PostMapping("indexed")
    public ResponseAPI<String> indexBlog() {
        boolean result = blogCategoryService.indexBlogCategory();
        String message = String.valueOf(result);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<BlogCategoryResponse> editBlogCategory(
            @PathVariable("id") Long id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newImg", required = false) List<MultipartFile> newImg) {

        String message_succed = "Cập nhập danh mục bài viết thành công";
        String message_failed = "Cập nhập danh mục bài viết thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateBlogCategoryRequest blogRequest = objectMapper.readValue(requestJson, UpdateBlogCategoryRequest.class);
            if (newImg != null) {
                blogRequest.setNewImg(newImg);
            }

            BlogCategoryResponse result = blogCategoryService.update(id, blogRequest);

            log.info("UPDATE BLOG-Category REQUEST SUCCESS");
            return ResponseAPI.<BlogCategoryResponse>builder()
                    .code(HttpStatus.OK.value())
                    .data(result)
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("UPDATE BLOG-Category ERROR: " + e.getMessage());
            return ResponseAPI.<BlogCategoryResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }


//
//    @PatchMapping("/edit/{id}")
//    public ResponseAPI<BlogCategoryResponse> updateBlogCategory(@PathVariable Long id ,@RequestBody UpdateBlogCategoryRequest request){
//        String message = "Update blog category successfully";
//        BlogCategoryResponse result = blogCategoryService.update(id, request);
//        log.info("UPDATE BLOG CATEGORY REQUEST");
//        return ResponseAPI.<BlogCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
//    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllBlogCategory(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "8") int size,
                                                               @RequestParam(defaultValue = "position") String sortKey,
                                                               @RequestParam(defaultValue = "desc") String sortValue,
                                                               @RequestParam(defaultValue = "ALL") String status,
                                                               @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List Blog Category";
        log.info("GET ALL BLOGS");
        Map<String, Object> result = blogCategoryService.getAll(page, size, sortKey, sortValue, status, keyword);
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteBlogCategory(@PathVariable Long id) {
        String message_succed = "Xóa thành công";
        String message_failed = "Xóa thất bại";
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
        String message_succed = "Xóa thành công";
        String message_failed = "Xóa thất bại";
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
        String message_succed = "Phục hồi thành công";
        String message_failed = "Phục hồi thất bại";
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
    public ResponseAPI<String> updateBlogCategory(@RequestBody Map<String, Object> requestBlogCategory) {

        if (!requestBlogCategory.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        // Lấy danh sách ID dưới dạng List<Integer>
        List<Integer> intIds = (List<Integer>) requestBlogCategory.get("id");

        // Chuyển đổi sang List<Long>
        List<Long> ids = intIds.stream().map(Long::valueOf).collect(Collectors.toList());

        String status = requestBlogCategory.get("status").toString();

        var result = blogCategoryService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBlogCategory(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "Xóa thành công";
        String message_failed = "Xóa thất bại";
        var result = blogCategoryService.delete(ids);
        if (result) {
            log.info("BlogCategory delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("BlogCategory delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<BlogCategoryResponse>> showBlogCategory() {
        return ResponseAPI.<List<BlogCategoryResponse>>builder().code(HttpStatus.OK.value()).data(blogCategoryService.getAll()).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<BlogCategoryResponse> getBlog(@PathVariable("id") Long id) {
        BlogCategoryResponse result = blogCategoryService.showDetail(id);
        return ResponseAPI.<BlogCategoryResponse>builder().code(HttpStatus.OK.value()).data(result).build();
    }

/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
