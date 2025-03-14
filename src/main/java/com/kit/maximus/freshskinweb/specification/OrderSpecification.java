package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("orderStatus"), status);
    }

    public static Specification<OrderEntity> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String pattern = keyword.trim() + "%"; // Sử dụng tiền tố thay vì contains
            return cb.or(
                    cb.like(root.get("firstName"), pattern),
                    cb.like(root.get("lastName"), pattern)
            );
        };
    }

    public static Specification<OrderEntity> hasOrderId(String orderId) {
        return (root, query, cb) ->
                (orderId == null || orderId.trim().isEmpty()) ? null :
                        cb.equal(root.get("orderId"), orderId.trim());
    }

    public static Specification<OrderEntity> getAllOrdersSpec(OrderStatus status, String keyword, String orderId) {
        return (root, query, cb) -> {
            // Thêm fetch join để load eager các relationship
            if (query.getResultType() == OrderEntity.class) {
                root.fetch("orderItems", JoinType.LEFT)
                        .fetch("productVariant", JoinType.LEFT)
                        .fetch("product", JoinType.LEFT);
            }

            return Specification.where(hasStatus(status))
                    .and(hasKeyword(keyword))
                    .and(hasOrderId(orderId))
                    .toPredicate(root, query, cb);
        };
    }
}