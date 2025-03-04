//package com.kit.maximus.freshskinweb.service;
//
//import com.kit.maximus.freshskinweb.dto.request.notification.CreationNotificationRequest;
//import com.kit.maximus.freshskinweb.dto.request.notification.UpdationNotificationRequest;
//import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
//import com.kit.maximus.freshskinweb.entity.NotificationEntity;
//import com.kit.maximus.freshskinweb.mapper.NotificationMapper;
//import com.kit.maximus.freshskinweb.repository.NotificationRepository;
//import com.kit.maximus.freshskinweb.repository.OrderRepository;
//import com.kit.maximus.freshskinweb.repository.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import javax.management.Notification;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@RequiredArgsConstructor
//public class NotificationService implements BaseService<NotificationResponse, CreationNotificationRequest, UpdationNotificationRequest, Long> {
//
//    NotificationRepository notificationRepository;
//    NotificationMapper notificationMapper;
//    UserRepository userRepository;
//    OrderRepository orderRepository;
//
//
////    @Override
////    public boolean add(CreationNotificationRequest request) {
////        NotificationEntity entity = notificationMapper.toNotificationEntity(request);
////
////        if(request.getUserId() != null) {
////            entity.setUser(userRepository.findById(request.getUserId()).orElse(null));
////        }
////
////        if(request.getOrderId() != null) {
////            entity.setOrder(orderRepository.findById(request.getOrderId()).orElse(null));
////        }
////        notificationRepository.save(entity);
////            return true;
////    }
//
//    @Override
//    public NotificationResponse update(Long aLong, UpdationNotificationRequest request) {
//        return null;
//    }
//
//    @Override
//    public String update(List<Long> id, String status) {
//        return "";
//    }
//
//    @Override
//    public boolean delete(Long aLong) {
//        return false;
//    }
//
//    @Override
//    public boolean delete(List<Long> longs) {
//        return false;
//    }
//
//    @Override
//    public boolean deleteTemporarily(Long aLong) {
//        return false;
//    }
//
//    @Override
//    public boolean restore(Long aLong) {
//        return false;
//    }
//
//    @Override
//    public NotificationResponse showDetail(Long aLong) {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
//        return Map.of();
//    }
//
//    @Override
//    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
//        return Map.of();
//    }
//}
