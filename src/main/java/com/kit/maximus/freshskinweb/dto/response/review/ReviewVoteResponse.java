package com.kit.maximus.freshskinweb.dto.response.review;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewVoteResponse {
    Long id;

    Long reviewId; //id reivewID

    Long userId; //id của user

    int voteType;

}
