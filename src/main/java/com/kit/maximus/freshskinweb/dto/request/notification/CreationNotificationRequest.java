package com.kit.maximus.freshskinweb.dto.request.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreationNotificationRequest {

    Long userId;
    Long orderId;
    String message;
    boolean isRead;

}
