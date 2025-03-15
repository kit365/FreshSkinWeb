package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    public static Specification<SkinCareRoutineEntity> hasActiveSkinType(Long skinTypeId) {
        return (root, query, cb) -> {
            if (skinTypeId == null) return null;
            return cb.and(
                    cb.equal(root.get("skinType").get("id"), skinTypeId),
                    cb.equal(root.get("skinType").get("status"), Status.ACTIVE)
            );
        };
    }

    public static Specification<SkinCareRoutineEntity> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<SkinCareRoutineEntity> hasCategories(List<String> categories) {
        return (root, query, cb) -> root.get("category").in(categories);
    }
}
