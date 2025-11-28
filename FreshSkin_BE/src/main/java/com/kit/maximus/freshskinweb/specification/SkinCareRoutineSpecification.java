package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SkinCareRoutineSpecification {
    public static Specification<SkinCareRoutineEntity> filterByStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return null;
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<SkinCareRoutineEntity> filterByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null) return null;
            Join<SkinCareRoutineEntity, SkinTypeEntity> skinTypeJoin = root.join("skinTypeEntity", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(skinTypeJoin.get("type")),
                    "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<SkinCareRoutineEntity> sortByUpdatedAt() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<SkinCareRoutineEntity> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<ProductEntity> getProductsBySkinTypeAndCategories(Long skinTypeId, List<String> categories) {
        return (root, query, cb) -> {
            Join<ProductEntity, SkinTypeEntity> skinTypeJoin = root.join("skinTypes");
            Join<ProductEntity, ProductCategoryEntity> categoryJoin = root.join("category");

            // Điều kiện cho skinType
            Predicate skinTypePredicate = cb.equal(skinTypeJoin.get("id"), skinTypeId);

            // Sử dụng title thay vì name
            Expression<String> categoryExpression = categoryJoin.get("title");

            // Điều kiện category nằm trong danh sách
            Predicate categoryPredicate = categoryExpression.in(categories);

            // Thêm điều kiện không bị xóa
            Predicate notDeleted = cb.equal(root.get("deleted"), false);

            // Sắp xếp theo thứ tự category
            query.orderBy(cb.asc(cb.function(
                    "FIELD",
                    Integer.class,
                    categoryExpression,
                    cb.literal(String.join(",", categories))
            )));

            return cb.and(skinTypePredicate, categoryPredicate, notDeleted);
        };
    }

}
