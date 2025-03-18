package com.kit.maximus.freshskinweb.controller.admin;


import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
import com.kit.maximus.freshskinweb.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/notify")
@Slf4j
public class NotificationAdminController {

    NotificationService notificationService;

    @GetMapping("show")
    public List<NotificationResponse> show() {
        return notificationService.show();
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        notificationService.delete(id);
    }

}
