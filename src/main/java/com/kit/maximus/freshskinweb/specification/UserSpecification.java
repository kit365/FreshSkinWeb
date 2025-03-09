package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;


public class UserSpecification {

    public static Specification<UserEntity> filterUsers(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                Predicate firstNamePredicate = criteriaBuilder.like(root.get("firstName"), likePattern);
                Predicate lastNamePredicate = criteriaBuilder.like(root.get("lastName"), likePattern);
                Predicate usernamePredicate = criteriaBuilder.like(root.get("username"), likePattern);

                // Thêm điều kiện tìm kiếm theo `username`
                return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, usernamePredicate);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<UserEntity> hasRole() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("role"));
    }
}
