package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewCreateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewResponse;
import com.kit.maximus.freshskinweb.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/reviews")
public class ReviewHomeController {

    ReviewService reviewService;

    @PostMapping("/create")
    public ResponseAPI<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {
        String message = "Tạo đánh giá thành công";
        reviewService.addReview(reviewCreateRequest);
        return ResponseAPI.<ReviewResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }


    @PatchMapping("/update/{id}")
    public ResponseAPI<ReviewResponse> updateReview(@PathVariable("id") Long id, @RequestBody @Valid ReviewUpdateRequest request) {
        String message = "Thay đổi đánh giá thành công";
        var review = reviewService.updateReview(id, request);
        return ResponseAPI.<ReviewResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(review)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<ReviewResponse> delete(@PathVariable Long id) {
        String message = "Xóa đánh giá thành công!";
        reviewService.deleteReview(id);
        return ResponseAPI.<ReviewResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<?> showAllReview(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "size", defaultValue = "6") int size,
                                        @RequestParam(value = "sortKey", defaultValue = "createdAt") String sortKey,
                                        @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
                                        @PathVariable("id") long id) {
        try {
            return ResponseAPI.<Map<String,Object>>builder()
                    .code(HttpStatus.OK.value())
                    .data(reviewService.getAllByProductID(page,size,sortKey,sortDirection,id))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseAPI.<ReviewResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lỗi trong quá trình show bình luận")
                    .build();
        }

    }
}
