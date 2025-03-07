package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<UserEntity> filterUsers(Status status, String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo status nếu có
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Lọc theo họ hoặc tên nếu keyword không rỗng
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern);
                predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
