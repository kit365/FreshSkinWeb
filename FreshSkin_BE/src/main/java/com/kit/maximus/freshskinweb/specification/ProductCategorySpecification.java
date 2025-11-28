package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductCategorySpecification {


    public static Specification<ProductCategoryEntity> findCategoryByTitle(List<String> titles) {
        if (titles.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            return root.get("title").in(titles);
        };
    }


    public static Specification<ProductCategoryEntity> filterByStatus(Status status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<ProductCategoryEntity> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }


}
