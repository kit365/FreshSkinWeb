package com.kit.maximus.freshskinweb.dto.request.notification;

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
    String orderId;
    Long review;
    String message;
    Boolean isRead;

}
