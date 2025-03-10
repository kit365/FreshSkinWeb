package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("orderStatus"), status);
    }

    public static Specification<OrderEntity> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null; // Nếu không có từ khóa thì không lọc
            }
            String pattern = "%" + keyword.trim() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("firstName"), pattern),
                    criteriaBuilder.like(root.get("lastName"), pattern)
            );
        };
    }

    public static Specification<OrderEntity> hasOrderId(String orderId) {
        return (root, query, criteriaBuilder) ->
                (orderId == null || orderId.trim().isEmpty())
                        ? null
                        : criteriaBuilder.equal(root.get("orderId"), orderId.trim());
    }
}
