package com.kit.maximus.freshskinweb.controller.admin;


import com.kit.maximus.freshskinweb.dto.response.NotificationResponse;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.service.notification.NotificationService;
import com.kit.maximus.freshskinweb.service.users.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/notify")
@Slf4j
public class NotificationAdminController {

    NotificationService notificationService;

    @GetMapping("update/{id}")
    public void update(@PathVariable("id") Long id) {
        notificationService.updateStatus(id);
    }

    @GetMapping("/{id}")
    public List<NotificationResponse> showReviewNotification(@PathVariable("id") Long roleID) {
        return notificationService.showNotification(roleID);
    }

    //xóa 1 thông báo
    @DeleteMapping("/delete/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.delete(id);
    }

    //xóa tất cả thông báo(chưa đọc
    @DeleteMapping("deleteAll/{id}")
    public void deleteAllNotification(@PathVariable("id") Long roleID) {
        notificationService.deleteAllReview(roleID);
    }

}
