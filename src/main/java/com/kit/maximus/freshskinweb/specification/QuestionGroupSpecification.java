package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.QuestionGroupEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.jpa.domain.Specification;

public class QuestionGroupSpecification {

    public static Specification<QuestionGroupEntity> hasTitle(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
    }

    public static Specification<QuestionGroupEntity> hasStatus(Status status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

}