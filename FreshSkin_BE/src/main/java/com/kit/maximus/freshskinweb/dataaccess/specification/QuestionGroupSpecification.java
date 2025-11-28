package com.kit.maximus.freshskinweb.dataaccess.specification;


import com.kit.maximus.freshskinweb.common.enums.Status;
import com.kit.maximus.freshskinweb.dataaccess.entity.QuestionGroupEntity;
import org.springframework.data.jpa.domain.Specification;

public class QuestionGroupSpecification {
    public static Specification<QuestionGroupEntity> hasTitle(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null) {
                return null;
            }
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }
    public static Specification<QuestionGroupEntity> hasStatus(Status status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }
}