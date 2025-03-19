package com.kit.maximus.freshskinweb.service;


import com.kit.maximus.freshskinweb.dto.request.review.ReviewCreateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.review.ReviewResponse;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
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
import com.kit.maximus.freshskinweb.service.notification.NotificationEvent;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    ApplicationEventPublisher eventPublisher;

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

            if (request.getParentId() == null) {
                autoReply(review);
            }

            reviewRepository.save(review);

            NotificationEntity notification = new NotificationEntity();
            notification.setUser(user);
            notification.setReview(review);
            notification.setMessage("Bạn có một đánh giá mới trên sản phẩm: " + product.getTitle());
            eventPublisher.publishEvent(new NotificationEvent(this, notification));

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void autoReply(ReviewEntity parentReview) {
        ReviewEntity review = new ReviewEntity();
        String comment = switch (parentReview.getRating()) {
            case 1 ->
                    "Chúng tôi rất tiếc vì sản phẩm/dịch vụ của chúng tôi không đáp ứng được kỳ vọng của bạn. Chúng tôi sẽ cải thiện để phục vụ bạn tốt hơn trong tương lai.";
            case 2 ->
                    "Cảm ơn bạn đã đánh giá. Chúng tôi sẽ tiếp thu và cải thiện để mang đến trải nghiệm tốt hơn cho bạn.";
            case 3 ->
                    "Cảm ơn bạn đã chia sẻ ý kiến. Chúng tôi sẽ cố gắng hơn nữa để cải thiện và đáp ứng nhu cầu của bạn.";
            case 4 ->
                    "Cảm ơn bạn đã hài lòng với sản phẩm/dịch vụ của chúng tôi. Chúng tôi rất vui khi nhận được đánh giá tích cực từ bạn!";
            case 5 ->
                    "Cảm ơn bạn rất nhiều vì đã đánh giá 5 sao! Chúng tôi rất hạnh phúc khi mang lại trải nghiệm tuyệt vời cho bạn.";
            default -> "Cảm ơn bạn đã chia sẻ ý kiến. Chúng tôi sẽ nỗ lực để cải thiện sản phẩm/dịch vụ của mình.";
        };

        UserEntity user = userRepository.findByEmail("nguyenhuuphuoc@gmail.com");
        review.setUser(user);
        review.setComment(comment);
        review.setProduct(parentReview.getProduct());
        review.setParent(parentReview);
        reviewRepository.save(review);
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
                        .withFirstName(response.getUser().getFirstName())
                        .withLastName(response.getUser().getLastName())
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


    public Map<String, Object> getAllByProductID(int page, int size, String sortKey, String sortDirection, long id) {
        Map<String, Object> map = new HashMap<>();

        // Tính toán phân trang
        int p = (page > 0) ? page - 1 : 0;
        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        Pageable pageable = PageRequest.of(p, size, sort);

        // Truy vấn lại Reviews cho sản phẩm theo slug và các điều kiện
        Page<ReviewEntity> reviewEntities = reviewRepository.findAllByParentIsNullAndStatusAndDeletedAndProduct_Id(pageable, Status.ACTIVE, false, id);

        // Chuyển các ReviewEntity thành ReviewResponse (DTO)
        List<ReviewResponse> responses = reviewEntities.stream()
                .map(this::convertToReviewResponse)
                .toList();


        //số review
        Integer count = reviewRepository.countAllByParentIsNullAndProduct_Id(id);


        //tổng rating
        int sum = responses.stream()
                .filter(r -> r.getRating() > 0 && r.getParent() == null)
                .mapToInt(ReviewResponse::getRating)
                .sum();

        double result;
        result = (sum > 0 && count > 0) ? Math.round((double) sum / count * 10.0) / 10.0 : 0.0;


        // Thêm kết quả vào Map để trả về
        Map<String, Object> pageMap = new HashMap<>();
        map.put("reviews", responses);
        pageMap.put("currentPage", reviewEntities.getNumber() + 1);
        pageMap.put("totalItems", reviewEntities.getTotalElements());
        pageMap.put("totalPages", reviewEntities.getTotalPages());
        pageMap.put("pageSize", reviewEntities.getSize());
        pageMap.put("rating", result);
        map.put("page", pageMap);
        return map;
    }

    public Map<String, Object> getAllByProduct(int page, int size, String sortKey, String sortDirection) {
        Map<String, Object> map = new HashMap<>();
        int p = (page > 0) ? page - 1 : 0;
        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<ReviewEntity> reviewEntities = reviewRepository.findAllByParentIsNullAndStatusAndDeleted(pageable, Status.ACTIVE, false);

        List<ReviewResponse> responses = reviewEntities.stream()
                .map(this::convertToReviewResponse)
                .toList();


        map.put("reviews", responses);
        map.put("currentPage", reviewEntities.getNumber() + 1);
        map.put("totalItems", reviewEntities.getTotalElements());
        map.put("totalPages", reviewEntities.getTotalPages());
        map.put("pageSize", reviewEntities.getSize());
        return map;
    }


    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    //dashboard data

    public long countReview() {
        return reviewRepository.count();
    }


}
