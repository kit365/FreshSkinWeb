package com.kit.maximus.freshskinweb.service.notification;

import com.kit.maximus.freshskinweb.dto.request.notification.CreationNotificationRequest;
import com.kit.maximus.freshskinweb.dto.request.notification.UpdationNotificationRequest;
import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.entity.review.ReviewEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.NotificationMapper;
import com.kit.maximus.freshskinweb.repository.NotificationRepository;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.repository.review.ReviewRepository;
import com.kit.maximus.freshskinweb.service.BaseService;
import com.kit.maximus.freshskinweb.specification.NotificationSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService implements BaseService<NotificationResponse, CreationNotificationRequest, UpdationNotificationRequest, Long> {

    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    OrderRepository orderRepository;
    ReviewRepository reviewRepository;
    ApplicationEventPublisher eventPublisher; //công cụ phát sự kiện,


    @Override
    public boolean add(CreationNotificationRequest request) {
        NotificationEntity entity = notificationMapper.toNotificationEntity(request);

        // Xử lý user notification
        if (request.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            entity.setUser(userEntity);
        }

        // Xử lý order notification
        if (request.getOrderId() != null) {
            OrderEntity orderEntity = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            entity.setOrder(orderEntity);

            if (orderEntity.getOrderStatus() != null) {
                entity.setMessage(orderEntity.getOrderStatus().getMessage());
            }
        }

        // Xử lý review notification
        if (request.getReview() != null) {
            ReviewEntity reviewEntity = reviewRepository.findById(request.getReview())
                    .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
            entity.setReview(reviewEntity);


            String reviewMessage = STR."\{reviewEntity.getUser().getUsername()} đã trả lời tin nhắn của bạn";
            entity.setMessage(reviewMessage);
        }

        // Nếu message được set trong request
        if (request.getMessage() != null) {
            entity.setMessage(request.getMessage());
        }

//        notificationRepository.save(entity); //nếu bị deplay th bỏ ở bên kia mà lưu ở đay

        //Phát event khi lưu
        eventPublisher.publishEvent(new NotificationEvent(this, entity));

        return true;
    }


    @Override
    public NotificationResponse update(Long id, UpdationNotificationRequest request) {
        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        entity.setIsRead(true);
        NotificationEntity savedEntity = notificationRepository.save(entity);

        NotificationResponse response = new NotificationResponse();
        response.setId(savedEntity.getId());
        response.setMessage(savedEntity.getMessage());
        response.setIsRead(savedEntity.getIsRead());
        response.setTime(savedEntity.getTime());
//        response.setDeleted(savedEntity.isDeleted());
        response.setStatus(savedEntity.getStatus().name());

//        if (savedEntity.getUser() != null) {
//            response.setUsername(savedEntity.getUser().getUsername());
//        }
//        if (savedEntity.getOrder() != null) {
//            response.setOrder(String.valueOf(savedEntity.getOrder().getOrderId()));
//        }
//        if (savedEntity.getReview() != null) {
//            response.setReview(savedEntity.getReview().getReviewId());
//        }

        return response;
    }

    public Map<String, Object> getAllByUser(Long userId, int page, int size) {
        Sort sort = Sort.by(
                Sort.Order.asc("isRead"),
                Sort.Order.desc("time")
        );

        PageRequest pageable = PageRequest.of(page, size, sort);

        // Sử dụng Specification để lọc theo userId
        Specification<NotificationEntity> spec = NotificationSpecification.hasUserId(userId);
        Page<NotificationEntity> entityPage = notificationRepository.findAll(spec, pageable);

        List<NotificationResponse> notifications = entityPage.getContent().stream()
                .map(entity -> {
                    NotificationResponse response = new NotificationResponse();
                    response.setId(entity.getId());
                    response.setMessage(entity.getMessage());
                    response.setIsRead(entity.getIsRead());
                    response.setTime(entity.getTime());
//                    response.setDeleted(entity.isDeleted());
                    response.setStatus(entity.getStatus().name());

//                    if (entity.getUser() != null) {
//                        response.setUsername(entity.getUser().getUsername());
//                    }
//                    if (entity.getOrder() != null) {
//                        response.setOrder(String.valueOf(entity.getOrder().getOrderId()));
//                    }
//                    if (entity.getReview() != null) {
//                        response.setReview(entity.getReview().getReviewId());
//                    }

                    return response;
                })
                .collect(Collectors.toList());

        return Map.of(
                "notifications", notifications,
                "page", entityPage.getNumber(),
                "totalElements", entityPage.getTotalElements(),
                "totalPages", entityPage.getTotalPages()
        );
    }

    @Override
    public String update(List<Long> id, String status) {
        return "";
    }

    @Override
    public boolean delete(Long aLong) {
        notificationRepository.deleteById(aLong);
        return true;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long aLong) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        return false;
    }

    @Override
    public NotificationResponse showDetail(Long aLong) {
        return null;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Sort sort = Sort.by(
                Sort.Order.asc("isRead"),
                Sort.Order.desc("time")
        );

        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<NotificationEntity> entityPage = notificationRepository.findAll(pageable);

        List<NotificationResponse> notifications = entityPage.getContent().stream()
                .map(entity -> {
                    NotificationResponse response = new NotificationResponse();
                    response.setId(entity.getId());
                    response.setMessage(entity.getMessage());
                    response.setIsRead(entity.getIsRead());
                    response.setTime(entity.getTime());
//                    response.setDeleted(entity.isDeleted());
                    response.setStatus(entity.getStatus().name());

//                    if (entity.getUser() != null) {
//                        response.setUsername(entity.getUser().getUsername());
//                    }
//                    if (entity.getOrder() != null) {
//                        response.setOrder(String.valueOf(entity.getOrder().getOrderId()));
//                    }
//                    if (entity.getReview() != null) {
//                        response.setReview(entity.getReview().getReviewId());
//                    }

                    return response;
                })
                .collect(Collectors.toList());

        return Map.of(
                "notifications", notifications,
                "page", entityPage.getNumber(),
                "totalElements", entityPage.getTotalElements(),
                "totalPages", entityPage.getTotalPages()
        );
    }

    //số tin nhắn chưa đọc(admin)
    public long countMessageFeedbackIsNotRead() {
        return notificationRepository.countByIsReadAndOrderIsNull(false);
    }

    public List<NotificationResponse> show() {

        List<NotificationEntity> request = notificationRepository.findAllByOrderIsNull();

        List<NotificationResponse> responses = new ArrayList<>();
        System.out.println(request.getFirst().getTime());
        request.forEach(entity -> {
            NotificationResponse response = new NotificationResponse();
            response.setId(entity.getId());
            response.setMessage(entity.getMessage());
            response.setIsRead(entity.getIsRead());
            response.setTime(entity.getTime());
            response.setSlugProduct(entity.getReview().getProduct().getSlug());
            responses.add(response);
        });
        return responses;
    }


    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }
}
