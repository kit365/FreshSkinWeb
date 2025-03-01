package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.ReviewResponse;
import com.kit.maximus.freshskinweb.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/reviews")
public class ReviewController {

    ReviewService reviewService;

    @PostMapping("/create")
    public ResponseAPI<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        String message = "Tạo đánh giá thành công";
        reviewService.addReview(reviewRequest);
        return ResponseAPI.<ReviewResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<ReviewResponse>> showReview() {
        String message = "Hiện tất cả đánh giá thành công";
        return ResponseAPI.<List<ReviewResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(reviewService.getAllReview())
                .build();
    }

    @PatchMapping("/update/{id}")
    public ResponseAPI<ReviewResponse> updateReview(@PathVariable("id") Long id, @RequestBody @Valid  ReviewRequest reviewRequest) {
        String message = "Thay đổi đánh giá thành công";
        var review = reviewService.updateReview(id, reviewRequest);
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
}
