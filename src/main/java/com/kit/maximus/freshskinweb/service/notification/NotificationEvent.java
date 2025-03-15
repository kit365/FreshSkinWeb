package com.kit.maximus.freshskinweb.service.notification;


import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

//sự kiện tùy chỉnh, chứa thông tin cần thiết khi thông báo được tạo

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEvent extends ApplicationEvent {

    NotificationEntity notification;

    public NotificationEvent(Object source, NotificationEntity notification) {
        super(source);
        this.notification = notification;
    }

    public NotificationEvent(Object source, Clock clock, NotificationEntity notification) {
        super(source, clock);
        this.notification = notification;
    }
}
