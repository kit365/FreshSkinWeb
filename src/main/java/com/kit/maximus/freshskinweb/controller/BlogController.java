package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.repository.BlogRepository;
import com.kit.maximus.freshskinweb.service.BlogService;
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
@RequestMapping("/admin/blog")
public class BlogController {

    BlogService blogService;

    @PostMapping("/create")
    public ResponseAPI<BlogResponse> createBlog(@RequestBody BlogCreationRequest request) {
        String message = "Create blog successfully ";
        var result = blogService.add(request);
        log.info("CREATE BLOG REQUEST");
        return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @PatchMapping("/edit/{id}")
    public ResponseAPI<BlogResponse> updateBlog(@PathVariable Long id,@RequestBody BlogUpdateRequest request) {
        String message = "Update blog successfully ";
        var result = blogService.update(id, request);
        log.info("UPDATE BLOG REQUEST");
        return ResponseAPI.<BlogResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
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
}
