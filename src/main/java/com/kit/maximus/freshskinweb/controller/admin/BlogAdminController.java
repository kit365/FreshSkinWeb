package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.BlogService;
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
@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/admin/blogs")
public class BlogAdminController {

    BlogService blogService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductResponseDTO> createProduct(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnail", required = false) List<MultipartFile> images) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", images);
        String message_succed = "Tạo bài viết thành công";
        String message_failed = "Tạo mục bài viết thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BlogCreationRequest blogRequest = objectMapper.readValue(requestJson, BlogCreationRequest.class);
            blogRequest.setThumbnail(images);

            var result = blogService.add(blogRequest);

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

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllBlog(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "8") int size,
                                                       @RequestParam(defaultValue = "position") String sortKey,
                                                       @RequestParam(defaultValue = "desc") String sortValue,
                                                       @RequestParam(defaultValue = "ALL") String status,
                                                       @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List Blog";
        log.info("GET ALL BLOGS");
        Map<String, Object> result = blogService.getAll(page, size,sortKey, sortValue,status,keyword);
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateBlog(@RequestBody Map<String,Object>  blogRequest) {

        if(!blogRequest.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_BLOGID);
        }

        List<Long> ids =  (List<Long>) blogRequest.get("id");
        String status  =  blogRequest.get("status").toString();

        var result = blogService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("/edit/{id}")
    public ResponseAPI<BlogResponse> updateBlog(@PathVariable Long id,@RequestBody BlogUpdateRequest request) {
        String message_succed = "Update Blog successfull";
        String message_failed = "Update Blog failed";
        var result = blogService.update(id, request);
        if (result != null) {
            log.info("Blog updated successfully");
            return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
        }
        log.info("Blog update failed");
        return ResponseAPI.<BlogResponse>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<String> deleteBlog(@PathVariable Long id) {
        String message_succed = "Delete Blog Category successfull";
        String message_failed = "Delete Blog Category failed";
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
        String message_succed = "Delete Blog successfull";
        String message_failed = "Delete Blog failed";
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
        String message_succed = "Restore Blog successfull";
        String message_failed = "Restore Blog failed";
        var result = blogService.restore(id);
        if (result) {
            log.info(" Blog Restored successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info(" Blog Restored failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteBlog(@RequestBody Map<String,Object> blogRequest) {

        if(!blogRequest.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_BLOGID);
        }

        List<Long> ids = (List<Long>) blogRequest.get("id");

        String message_succed = "delete Product successfull";
        String message_failed = "delete Product failed";
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
        return  ResponseAPI.<List<BlogResponse>>builder().code(HttpStatus.OK.value()).data(blogService.getAll()).build();
    }

    @GetMapping("{id}")
    public ResponseAPI<BlogResponse> getBlog(@PathVariable("id") Long id) {
        BlogResponse result = blogService.showDetail(id);
        return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).data(result).build();
    }

}
