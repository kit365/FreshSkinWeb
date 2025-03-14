package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.review.ReviewCreateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewResponse;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewVoteResponse;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
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
import com.kit.maximus.freshskinweb.repository.search.ProductSearchRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    @Transactional
    public void addVote(ReviewVoteRequest request) {
        // Tìm review
        ReviewEntity review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Tìm vote của user trong danh sách votes của review
        ReviewVoteEntity existingVote = review.getVotes().stream()
                .filter(v -> v.getUserId().equals(request.getUserId()))
                .findFirst()
                .orElse(null);

        if (existingVote != null) {
            // Nếu đã vote, cập nhật lại voteType
            existingVote.setVoteType(request.getVoteType());
            reviewVoteRepository.save(existingVote);
        } else {
            // Nếu chưa vote, tạo mới
            ReviewVoteEntity reviewVoteEntity = new ReviewVoteEntity();
            reviewVoteEntity.setReview(review);
            reviewVoteEntity.setUserId(request.getUserId());
            reviewVoteEntity.setVoteType(request.getVoteType());

            reviewVoteRepository.save(reviewVoteEntity);
        }
    }


    @Transactional
    public void addReview(ReviewCreateRequest request) {
        ReviewEntity review = reviewMapper.toReviewEntity(request);

        // Sử dụng CompletableFuture để thực hiện các truy vấn song song
        CompletableFuture<UserEntity> userFuture = CompletableFuture.supplyAsync(() ->
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
        );

        CompletableFuture<ProductEntity> productFuture = CompletableFuture.supplyAsync(() ->
                productRepository.findWithReviewsById(request.getProductId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND))
        );

        // Kiểm tra nếu có parent review
        if (request.getParentId() != null) {
            ReviewEntity parentReview = reviewRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_REVIEW_NOT_FOUND));
            review.setParent(parentReview);
        }

        // Chờ kết quả từ cả hai truy vấn song song
        try {
            UserEntity user = userFuture.get();
            ProductEntity product = productFuture.get();

            review.setUser(user);
            review.setProduct(product);

            reviewRepository.save(review);

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    public List<ReviewResponse> getAllReview() {
        List<ReviewEntity> reviewEntities = reviewRepository.findAllByParentIsNull();

        return reviewEntities.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse convertToReviewResponse(ReviewEntity reviewEntity) {
        // Đếm số like/dislike riêng cho từng review (cha hoặc con)
        int like = 0, dislike = 0;
        for (ReviewVoteEntity vote : reviewEntity.getVotes()) {
            if (vote.getVoteType() == -1) {
                dislike++;
            } else if (vote.getVoteType() == 1) {
                like++;
            }
        }

        // Map dữ liệu chính cho review hiện tại
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(reviewEntity);
        reviewResponse.setProductId(reviewEntity.getProduct().getId());
        reviewResponse.setLikeCount(like);
        reviewResponse.setDislikeCount(dislike);
        setUserFields(reviewResponse);

        // Xử lý replies, đảm bảo mỗi reply cũng có số like/dislike riêng
        if (reviewEntity.getReplies() != null && !reviewEntity.getReplies().isEmpty()) {
            List<ReviewResponse> replyResponses = reviewEntity.getReplies().stream()
                    .map(reply -> {
                        ReviewResponse replyResponse = convertToReviewResponse(reply);
                        replyResponse.setParent(null); // Xóa parent để tránh dữ liệu dư thừa
                        return replyResponse;
                    })
                    .collect(Collectors.toList());

            reviewResponse.setReplies(replyResponses);
        }

        return reviewResponse;
    }



    // Phương thức chung để set các thuộc tính cho UserResponseDTO
    private void setUserFields(ReviewResponse response) {
        response.setUser(new UserResponseDTO()
                        .withAvatar(response.getUser().getAvatar())
                        .withUsername(response.getUser().getUsername())
                        .withUserID(response.getUser().getUserID())
//                .withRole(response.getUser().getRole())
                        .withToken(response.getUser().getToken())
        );
    }

    //HAM nay dang bug
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
