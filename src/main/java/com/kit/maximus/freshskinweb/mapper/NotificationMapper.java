package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.notification.CreationNotificationRequest;
import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
//    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "orders", ignore = true)
//    NotificationEntity toNotificationEntity(CreationNotificationRequest request);
//
//
//    NotificationResponse toNotificationResponse(NotificationEntity entity);
//
//    List<NotificationResponse> toNotificationResponse(List<NotificationEntity> entities);
//
//    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "orders", ignore = true)
//    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
//    void updateNotificationEntity(@MappingTarget  NotificationEntity entity, CreationNotificationRequest request);
}
