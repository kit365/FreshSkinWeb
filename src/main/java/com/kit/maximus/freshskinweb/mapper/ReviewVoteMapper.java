package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.entity.review.ReviewVoteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewVoteMapper {

    ReviewVoteEntity toReviewVoteEntity(ReviewVoteRequest reviewEntity);

}
