package com.kit.maximus.freshskinweb.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReviewVoteRequest {

    Long reviewId; //id reivewID

    Long userId; //id của user

    @Min(value = -1, message = "Vote type must be -1 (Dislike), 0 (Neutral), or 1 (Like).")
    @Max(value = 1, message = "Vote type must be -1 (Dislike), 0 (Neutral), or 1 (Like).")
    int voteType; // 1 = Like, -1 = Dislike, 0 = Hủy vote

}
