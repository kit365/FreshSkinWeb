package com.kit.maximus.freshskinweb.service.notification;

import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import com.kit.maximus.freshskinweb.repository.NotificationRepository;
import com.kit.maximus.freshskinweb.websocket.NotificationWebSocketHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component

//class này để xử lý các thông báo
public class NotificationListener {
    NotificationRepository notificationRepository;

    NotificationWebSocketHandler notificationWebSocketHandler;

    @Async
    @EventListener //lắng nghe và xử lý su kiện
    public void handleNotification(NotificationEvent event) throws IOException {
        //chứa object NotificationEntity
        NotificationEntity notification = event.getNotification();
        System.out.println("nhận thông báo:" + notification.getMessage());

        sendNotification(notification);
        notificationRepository.save(notification);

    }

    private void sendNotification(NotificationEntity notification) throws IOException {
        notificationWebSocketHandler.sendNotification(notification.getMessage());
    }


}
