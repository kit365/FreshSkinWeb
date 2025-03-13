package com.kit.maximus.freshskinweb.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReviewCreateRequest implements Serializable {

    @NotNull(message = "User ID is required")
    Long userId;

    @NotNull(message = "Product ID is required")
    Long productId;

    ReviewVoteRequest votes;

    @Min(value = 1, message = "Đánh giá thấp nhất là 1 sao")
    @Max(value = 5, message = "Đánh giá cao nhất là 5 sao")
    Integer rating;

    String comment;

    Long parentId;
}
