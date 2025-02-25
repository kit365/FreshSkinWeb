package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewRequest;
import com.kit.maximus.freshskinweb.dto.response.ReviewResponse;
import com.kit.maximus.freshskinweb.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewEntity toReviewEntity(ReviewRequest reviewEntity);

    ReviewResponse toReviewResponse(ReviewEntity reviewEntity);

//    @Mapping(target = "")
    void updateReviewEntity(ReviewRequest reviewRequest, @MappingTarget ReviewEntity reviewEntity);
}
