package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.entity.review.ReviewVoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewVoteMapper {


    @Mapping(target = "id", ignore = true)  // ID sẽ do DB tự sinh
    @Mapping(target = "review", ignore = true)  // Sẽ set review sau
    ReviewVoteEntity toReviewVoteEntity(ReviewVoteRequest request);

}
