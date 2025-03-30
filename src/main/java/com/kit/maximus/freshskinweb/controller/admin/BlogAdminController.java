package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.blog.BlogService;
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

@RestController
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/admin/blogs")
public class BlogAdminController {

    BlogService blogService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<?> createBlog(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnail", required = false) List<MultipartFile> images) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", images);
        String message_succed = "Tạo bài viết thành công";
        String message_failed = "Tạo bài viết thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BlogCreationRequest blogRequest = objectMapper.readValue(requestJson, BlogCreationRequest.class);
            blogRequest.setThumbnail(images);

            boolean result = blogService.add(blogRequest);

            log.info("CREATE BLOG REQUEST SUCCESS");
            return ResponseAPI.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("CREATE BLOG ERROR: " + e.getMessage());
            return ResponseAPI.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllBlog(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "8") int size,
                                                       @RequestParam(defaultValue = "position") String sortKey,
                                                       @RequestParam(defaultValue = "desc") String sortValue,
                                                       @RequestParam(defaultValue = "ALL") String status,
                                                       @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List Blog";
        Map<String, Object> result = blogService.getAll(page, size, sortKey, sortValue, status, keyword);
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("update/{id}")
    public ResponseAPI<String> updateBlog(@PathVariable("id") int id,
                                          @RequestBody Map<String, Object> request) {

        String statusEdit = (String) request.get("statusEdit");
        String status = (String) request.get("status");
        int position = request.containsKey("position") ? (int) request.get("position") : 0;

        String result = blogService.update(id, status, position, statusEdit);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateBlog(@RequestBody Map<String, Object> blogRequest) {

        if (!blogRequest.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_BLOGID);
        }

        List<Long> ids = (List<Long>) blogRequest.get("id");
        String status = blogRequest.get("status").toString();

        var result = blogService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }


    @PatchMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<BlogResponse> editBlog(
            @PathVariable("id") Long id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newImg", required = false) List<MultipartFile> newImg) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", newImg);
        String message_succed = "Cập nhập bài viết thành công";
        String message_failed = "Cập nhập bài viết thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BlogUpdateRequest blogRequest = objectMapper.readValue(requestJson, BlogUpdateRequest.class);
            if (newImg != null) {
                blogRequest.setNewImg(newImg);
            }

            BlogResponse result = blogService.update(id, blogRequest);

            log.info("UPDATE BLOG REQUEST SUCCESS");
            return ResponseAPI.<BlogResponse>builder()
                    .code(HttpStatus.OK.value())
                    .data(result)
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("UPDATE BLOG ERROR: " + e.getMessage());
            return ResponseAPI.<BlogResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<String> deleteBlog(@PathVariable Long id) {
        String message_succed = "Xóa thành công";
        String message_failed = "Xóa thất bại";
        boolean result = blogService.delete(id);
        if (result) {
            log.info(" Blog deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" Blog delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }


    @PatchMapping("/deleteT/{id}")
    public ResponseAPI<String> deleteTBlog(@PathVariable Long id) {
        String message_succed = "Xóa thành công";
        String message_failed = "Xóa thất bại";
        var result = blogService.deleteTemporarily(id);
        if (result) {
            log.info(" Blog Category deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" Blog Category deleted failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("/restore/{id}")
    public ResponseAPI<String> restoreBlog(@PathVariable Long id) {
        String message_succed = "Phục hồi thành công";
        String message_failed = "Phục hồi thất bại";
        var result = blogService.restore(id);
        if (result) {
            log.info(" Blog Restored successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" Blog Restored failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBlog(@RequestBody Map<String, Object> blogRequest) {

        if (!blogRequest.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_BLOGID);
        }

        List<Long> ids = (List<Long>) blogRequest.get("id");

        String message_succed = "Xóa thành công";
        String message_failed = "Xóa thất bại";
        var result = blogService.delete(ids);
        if (result) {
            log.info("Products delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Products delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<BlogResponse>> showBlogCategory() {
        return ResponseAPI.<List<BlogResponse>>builder().code(HttpStatus.OK.value()).data(blogService.getAll()).build();
    }

    @GetMapping("{id}")
    public ResponseAPI<BlogResponse> getBlog(@PathVariable("id") Long id) {
        BlogResponse result = blogService.showDetail(id);
        return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PostMapping("indexed")
    public ResponseAPI<String> indexBlog() {
        boolean result = blogService.indexBlogs();
        String message = String.valueOf(result);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

}
