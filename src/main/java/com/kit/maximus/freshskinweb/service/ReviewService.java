package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.review.ReviewCreateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewResponse;
import com.kit.maximus.freshskinweb.entity.review.ReviewEntity;
import com.kit.maximus.freshskinweb.entity.review.ReviewVoteEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ReviewMapper;
import com.kit.maximus.freshskinweb.mapper.ReviewVoteMapper;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.review.ReviewRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.repository.review.ReviewVoteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ReviewService {
    UserRepository userRepository;
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    ReviewVoteRepository reviewVoteRepository;
    ReviewMapper reviewMapper;
    ReviewVoteMapper reviewVoteMapper;


    //hàm này để vote like/dislike
    public void addVote(ReviewVoteRequest request) {
        ReviewEntity review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Nếu user tự vote cho review của mình -> báo lỗi
        if (review.getUser().getUserID().equals(request.getUserId())) {
            throw new AppException(ErrorCode.CANNOT_VOTE_OWN_REVIEW);
        }

//        // Kiểm tra xem user đã vote chưa
//        boolean hasVoted = reviewVoteRepository.existsByReviewIdAndUserId(request.getReviewId(), request.getUserId());

//
//        if (hasVoted) {
//            throw new AppException(ErrorCode.ALREADY_VOTED);
//        }

        ReviewVoteEntity reviewVoteEntity = reviewVoteMapper.toReviewVoteEntity(request);
        reviewVoteRepository.save(reviewVoteEntity);
    }

    //hàm này để chỉnh sửa vote
    public void updateVote(ReviewVoteRequest request, long id) {
        if (request.getVoteType() < -1 || request.getVoteType() > 1) {
            throw new AppException(ErrorCode.VOTE_STATUS_INVALID);
        }

        ReviewVoteEntity requestEntity = reviewVoteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));


        requestEntity.setVoteType(request.getVoteType());

        reviewVoteRepository.save(requestEntity);
    }


    public void addReview(ReviewCreateRequest request) {

        ReviewEntity review = reviewMapper.toReviewEntity(request);

        //tài khoản bình luận
        review.setUser(userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));

        //sản phẩm được review/feedback
        review.setProduct(productRepository.findById(request.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));

        if (request.getParentId() != null) {
            ReviewEntity parentReview = reviewRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_REVIEW_NOT_FOUND));
            review.setParent(parentReview);
        }

        reviewRepository.save(review);
    }


    public List<ReviewResponse> getAllReview() {

        List<ReviewEntity> reviewEntities = reviewRepository.findAllByParentIsNull();

        return reviewEntities.stream()
                .map(reviewMapper::toReviewResponse)
                .peek(this::setUserFields)
                .peek(response -> {
                    if (response.getReplies() != null) {
                        response.getReplies().forEach(this::setUserFields);
                    }
                })
                .collect(Collectors.toList());
    }

    // Phương thức chung để set các thuộc tính cho UserResponseDTO
    private void setUserFields(ReviewResponse response) {
        response.setUser(new UserResponseDTO()
                .withAvatar(response.getUser().getAvatar())
                .withUsername(response.getUser().getUsername())
                .withUserID(response.getUser().getUserID())
                .withRole(response.getUser().getRole())
                .withToken(response.getUser().getToken())
        );
    }

    public ReviewResponse updateReview(Long id, ReviewUpdateRequest reviewCreateRequest) {
        ReviewEntity review = reviewRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        reviewMapper.updateReviewEntity(reviewCreateRequest, review);

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long id) {
        ReviewEntity review = reviewRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }


}
