package com.kit.maximus.freshskinweb.dto.request.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdationNotificationRequest {

    Long id;
    Long userId;
    String orderId;
    String message;
    boolean isRead;

}
