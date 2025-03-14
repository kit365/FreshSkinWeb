//package com.kit.maximus.freshskinweb.specification;
//
//import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinResultSearchRequest;
//import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
//import com.kit.maximus.freshskinweb.entity.UserEntity;
//import com.kit.maximus.freshskinweb.utils.Status;
//import jakarta.persistence.criteria.Join;
//import jakarta.persistence.criteria.JoinType;
//import jakarta.persistence.criteria.Predicate;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkinTestSpecification {
//    public static Specification<SkinTestEntity> withFilters(SkinResultSearchRequest request) {
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            // Join với UserEntity
//            Join<SkinTestEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);
//
//            // Lọc theo status
//            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
//                predicates.add(cb.equal(root.get("status"), Status.valueOf(request.getStatus().toUpperCase())));
//            }
//
//            // Tìm kiếm theo firstName
//            if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
//                predicates.add(cb.like(cb.lower(userJoin.get("firstName")),
//                        "%" + request.getFirstName().toLowerCase() + "%"));
//            }
//
//            // Tìm kiếm theo lastName
//            if (request.getLastName() != null && !request.getLastName().isEmpty()) {
//                predicates.add(cb.like(cb.lower(userJoin.get("lastName")),
//                        "%" + request.getLastName().toLowerCase() + "%"));
//            }
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//    }
//}
