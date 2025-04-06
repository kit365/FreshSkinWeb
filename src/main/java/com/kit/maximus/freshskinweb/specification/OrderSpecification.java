package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return null;
            return criteriaBuilder.equal(root.get("orderStatus"), status);
        };
    }

    public static Specification<OrderEntity> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchTerm)
            );
        };
    }

    public static Specification<OrderEntity> hasOrderId(String orderId) {
        return (root, query, criteriaBuilder) -> {
            if (orderId == null || orderId.trim().isEmpty()) return null;
            return criteriaBuilder.equal(root.get("orderId"), orderId);
        };
    }

    public static Specification<OrderEntity> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) return null;
            return criteriaBuilder.equal(root.get("user").get("userID"), userId);
        };
    }

    public static Specification<OrderEntity> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }

    public static Specification<OrderEntity> orderByStatusPriorityAndDate(OrderStatus priorityStatus) {
        return (root, query, criteriaBuilder) -> {
            // Create CASE expression for status ordering
            Expression<Object> statusOrder = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.PENDING), 1)
                    .when(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.COMPLETED), 2)
                    .when(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.DELIVERING), 3)
                    .when(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.CANCELED), 4)
                    .otherwise(4);

            // If priority status is specified, modify the order
            if (priorityStatus != null) {
                statusOrder = criteriaBuilder.selectCase()
                        .when(criteriaBuilder.equal(root.get("orderStatus"), priorityStatus), 0)
                        .otherwise(statusOrder);
            }

            query.orderBy(
                    criteriaBuilder.asc(statusOrder),
                    criteriaBuilder.desc(root.get("updatedAt"))
            );

            return null;
        };
    }
}
