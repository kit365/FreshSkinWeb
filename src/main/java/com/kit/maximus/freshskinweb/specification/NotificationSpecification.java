package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class NotificationSpecification {

    // Sắp xếp theo thời gian tạo từ mới đến cũ
    public static Specification<NotificationEntity> sortByCreatedTime() {
        return (Root<NotificationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.orderBy(cb.desc(root.get("time"))); // Sắp xếp theo `Created_at` từ mới đến cũ
            return cb.conjunction();
        };
    }

    // Sau khi cập nhật trạng thái đơn hàng -> vẫn sắp xếp theo thời gian tạo
    public static Specification<NotificationEntity> filterByOrderStatus(OrderStatus status) {
        return (Root<NotificationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    // Khi đọc thông báo -> sắp xếp theo thời gian nhưng thông báo đã đọc bị xếp xuống cuối
    public static Specification<NotificationEntity> sortByReadStatus() {
        return (Root<NotificationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.orderBy(
                    cb.asc(root.get("is_read")),  // Thông báo đã đọc sẽ được xếp dưới
                    cb.desc(root.get("time"))     // Thông báo chưa đọc vẫn xếp theo thời gian mới nhất
            );
            return cb.conjunction();
        };
    }
}
