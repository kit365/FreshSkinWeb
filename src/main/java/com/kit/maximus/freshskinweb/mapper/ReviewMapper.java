package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewCreateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewResponse;
import com.kit.maximus.freshskinweb.entity.review.ReviewEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "votes", ignore = true)
        //tự set product vào feedback, mapper không hiểu đc
    ReviewEntity toReviewEntity(ReviewCreateRequest reviewEntity);


    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "dislikeCount", ignore = true)
    @Mapping(target = "productId", ignore = true)
        //tự set product vào entity, chỉ trả cho FE Id
    ReviewResponse toReviewResponse(ReviewEntity reviewEntity);


    //khi người dùng cập nhập bình luận -> thường user chỉ cần cập nhập lại comment, lượt sao, lượt like/dislike
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "votes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReviewEntity(ReviewUpdateRequest reviewUpdateRequest, @MappingTarget ReviewEntity reviewEntity);

}
