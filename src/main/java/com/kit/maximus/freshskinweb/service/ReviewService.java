package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.review.ReviewRequest;
import com.kit.maximus.freshskinweb.dto.response.ReviewResponse;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.ReviewEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ReviewMapper;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.ReviewRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ReviewService {
    UserRepository userRepository;
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;

    public boolean addReview(ReviewRequest reviewRequest) {

        ReviewEntity review = reviewMapper.toReviewEntity(reviewRequest);
        UserEntity user = userRepository.findById(reviewRequest.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user == null) {
            review.setUser(null);
        } else {
            review.setUser(user);
        }

        ProductEntity product = productRepository.findById(reviewRequest.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product == null) {
            review.setProduct(null);
        } else {
            review.setProduct(product);
        }

        reviewMapper.toReviewResponse(reviewRepository.save(review));
        return true;
    }

    public List<ReviewResponse> getAllReview() {
        return reviewRepository.findAll().stream().map(reviewMapper::toReviewResponse).collect(Collectors.toList());
    }

    public ReviewResponse updateReview(Long id, ReviewRequest reviewRequest) {
        var review = reviewRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        reviewMapper.updateReviewEntity(reviewRequest, review);

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long id) {
        ReviewEntity review = reviewRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }


}
