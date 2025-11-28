package com.kit.maximus.freshskinweb.service.notification;

import com.kit.maximus.freshskinweb.dto.request.notification.CreationNotificationRequest;
import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.entity.ReviewEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.NotificationMapper;
import com.kit.maximus.freshskinweb.repository.NotificationRepository;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.repository.review.ReviewRepository;
import com.kit.maximus.freshskinweb.service.users.RoleService;
import com.kit.maximus.freshskinweb.specification.NotificationSpecification;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    OrderRepository orderRepository;
    ReviewRepository reviewRepository;
    ApplicationEventPublisher eventPublisher; //công cụ phát sự kiện,
    RoleService roleService;

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


        //Phát event khi lưu
        eventPublisher.publishEvent(new NotificationEvent(this, entity));

        return true;
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
                    response.setStatus(entity.getStatus().name());

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


    public boolean delete(Long aLong) {
        notificationRepository.deleteById(aLong);
        return true;
    }


    public boolean delete(List<Long> longs) {
        return false;
    }


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
                    response.setStatus(entity.getStatus().name());
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

    public void updateStatus(Long id) {
        NotificationEntity entity = notificationRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        entity.setIsRead(true);
        notificationRepository.save(entity);
    }



    /*

     */

    //số tin nhắn chưa đọc(admin)
    public long countMessageFeedbackIsNotRead() {
        return notificationRepository.countByIsReadAndOrderIsNull(false);
    }

    //số tin  nhắn chưa đọc
    public long countMessageFeedback(long roleID) {
        try {
            RoleEntity role = roleService.getRoleEntityById(roleID);
            long numberMessage = switch (role.getTitle().toLowerCase()) {
                case "quản trị viên" -> notificationRepository.count();
                case "quản lý sản phẩm" -> notificationRepository.countByIsReadAndOrderIsNull(false);
                case "quản lý đơn hàng" -> notificationRepository.countByIsReadAndReviewIsNull(false);
                default -> 0;
            };
            return numberMessage;
        } catch (MethodArgumentTypeMismatchException e) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        } catch (Exception e) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
    }

    @NotNull
    private List<NotificationResponse> getNotificationResponses(List<NotificationEntity> request) {
        List<NotificationResponse> responses = new ArrayList<>();
        request.forEach(entity -> {
            NotificationResponse response = new NotificationResponse();
            response.setId(entity.getId());
            response.setMessage(entity.getMessage());
            response.setIsRead(entity.getIsRead());
            response.setTime(entity.getTime());
            if (entity.getReview() != null) {
                response.setSlugProduct(entity.getReview().getProduct().getSlug());
                response.setImage(entity.getReview().getProduct().getThumbnail().getFirst());
            }
            responses.add(response);
        });
        return responses;
    }

    @Transactional
    public void deleteAllReviewNotification() {
        notificationRepository.deleteAllByIsReadAndOrderIsNull(true);
    }

    /*
    orde
     */

    public long countMessageOrderIsNotRead() {
        return notificationRepository.countByIsReadAndReviewIsNull(false);
    }

    public List<NotificationResponse> showNotification(Long roleID) {
        try {
            RoleEntity role = roleService.getRoleEntityById(roleID);
            List<NotificationEntity> entityList;
            Sort sort = Sort.by(
                    Sort.Order.asc("isRead"),
                    Sort.Order.desc("time")
            );

            switch (role.getTitle().toLowerCase()) {
                case "quản trị viên":
                    entityList = notificationRepository.findAllByOrderByIsReadAscTimeDesc();
                    break;
                case "quản lý sản phẩm":
                    entityList = notificationRepository.findAllByOrderIsNull(sort);
                    break;
                case "quản lý đơn hàng":
                    entityList = notificationRepository.findAllByReviewIsNull(sort);
                    break;
                default:
                    throw new AppException(ErrorCode.ROLE_ACCESS_DENIED);
            }

            return getNotificationResponses(entityList);
        } catch (MethodArgumentTypeMismatchException e) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        } catch (Exception e) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteAllReview(Long id) {
        log.info(id.toString());
        RoleEntity role = roleService.getRoleEntityById(id);
        switch (role.getTitle().toLowerCase()) {
            case "quản trị viên":
                notificationRepository.deleteAllByIsRead(true);
                break;
            case "quản lý sản phẩm":
                notificationRepository.deleteAllByIsReadAndOrderIsNull(true);
                break;
            case "quản lý đơn hàng":
                notificationRepository.deleteAllByIsReadAndReviewIsNull(true);
                break;
            default:
                throw new AppException(ErrorCode.ROLE_ACCESS_DENIED);
        }

    }

    @Transactional
    public void deleteAllOrderNotification() {
        notificationRepository.deleteAllByIsReadAndReviewIsNull(true);
    }

}
