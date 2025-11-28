package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.notification.CreationNotificationRequest;
import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "review", ignore = true)
    NotificationEntity toNotificationEntity(CreationNotificationRequest request);

}
