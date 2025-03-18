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

    @GetMapping("update/{id}")
    public void update(@PathVariable("id") Long id) {
        notificationService.updateStatus(id);
    }

    /*
    Thông báo cho feedback/review
     */

    //show danh sách thông báo review
    @GetMapping("review")
    public List<NotificationResponse> showReviewNotification() {
        return notificationService.showReview();
    }

    //xóa 1 thông báo review
    @DeleteMapping("review/delete/{id}")
    public void deleteReviewNotification(@PathVariable Long id) {
        notificationService.delete(id);
    }

    //xóa tất cả thông báo(chưa đọc) review
    @DeleteMapping("review/deleteAll")
    public void deleteAllReviewNotification() {
        notificationService.deleteAllReviewNotification();
    }

    /// //////////////////////////////////////////////////////////

    /*
    Thông báo cho feedback/order
     */
    @GetMapping("order")
    public List<NotificationResponse> showOrderNotification() {
        return notificationService.showOrder();
    }

    //xóa 1 thông báo order
    @DeleteMapping("order/delete/{id}")
    public void deleteOrderNotification(@PathVariable Long id) {
        notificationService.delete(id);
    }

    //xóa tất cả thông báo(chưa đọc) order
    @DeleteMapping("order/deletedAll")
    public void deleteAllOrderNotification() {
        notificationService.deleteAllOrderNotification();
    }


}
