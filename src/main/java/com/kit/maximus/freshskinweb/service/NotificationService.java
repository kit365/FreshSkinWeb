package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.notification.CreationNotificationRequest;
import com.kit.maximus.freshskinweb.dto.request.notification.UpdationNotificationRequest;
import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.NotificationMapper;
import com.kit.maximus.freshskinweb.repository.NotificationRepository;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.specification.NotificationSpecification;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationService implements BaseService<NotificationResponse, CreationNotificationRequest, UpdationNotificationRequest, Long> {

    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    OrderRepository orderRepository;


    @Override
    public boolean add(CreationNotificationRequest request) {
        NotificationEntity entity = notificationMapper.toNotificationEntity(request);
        OrderEntity orderEntity = orderRepository.findById(request.getOrderId()).orElse(null);
        UserEntity userEntity = userRepository.findById(request.getUserId()).orElse(null);

        if(request.getUserId() != null) {
            entity.setUser(userEntity);
        }

        if(request.getOrderId() != null) {
            entity.setOrder(orderEntity);
        }

        if(orderEntity.getOrderStatus() != null) {
            entity.setMessage((orderEntity.getOrderStatus().getMessage()));
        }

        notificationRepository.save(entity);
            return true;
    }

    @Override
    public NotificationResponse update(Long aLong, UpdationNotificationRequest request) {
        NotificationEntity entity = notificationRepository.findById(aLong).orElseThrow(()-> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(request.getUserId()).orElse(null);
        OrderEntity orderEntity = orderRepository.findById(request.getOrderId()).orElse(null);

        if(request.getUserId() != null) {
            entity.setUser(userEntity);
        }

        if(request.getOrderId() != null) {
            entity.setOrder(orderEntity);
        }

        if(orderEntity.getOrderStatus() != null) {
            entity.setMessage((orderEntity.getOrderStatus().getMessage()));
        }

        notificationMapper.updateNotificationEntity(entity, request);
        return notificationMapper.toNotificationResponse(notificationRepository.save(entity));
    }

    public List<NotificationEntity> getSortedNotifications(OrderStatus status) {
        Specification<NotificationEntity> spec = Specification
                .where(NotificationSpecification.sortByCreatedTime())  // Sắp xếp theo thời gian
                .and(NotificationSpecification.filterByOrderStatus(status))  // Lọc theo trạng thái đơn hàng
                .and(NotificationSpecification.sortByReadStatus()); // Sắp xếp thông báo đã đọc xuống cuối

        return notificationRepository.findAll(spec);
    }

    @Override
    public String update(List<Long> id, String status) {
        return "";
    }

    @Override
    public boolean delete(Long aLong) {
        return false;
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
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }
}
