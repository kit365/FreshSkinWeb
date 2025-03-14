package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SkinCareRoutineSpecification {

    public static Specification<SkinCareRoutineEntity> filterByStatus(Status status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<SkinCareRoutineEntity> filterByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            Join<SkinCareRoutineEntity, SkinTypeEntity> skinTypeJoin = root.join("skinTypeEntity", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(skinTypeJoin.get("type")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<SkinCareRoutineEntity> sortByUpdatedAt() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            return criteriaBuilder.conjunction();
        };
    }
}
