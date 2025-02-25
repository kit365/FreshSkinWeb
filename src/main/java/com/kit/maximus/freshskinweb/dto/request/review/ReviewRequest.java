package com.kit.maximus.freshskinweb.dto.request.review;

import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReviewRequest implements Serializable {

    Long productId;

    Long userId;

    @Range(min = 1, max = 5, message = "RATING_INVALID")
    int rating;

    String comment;
}
