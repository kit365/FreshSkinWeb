package com.kit.maximus.freshskinweb.dataaccess.specification;

import com.kit.maximus.freshskinweb.dataaccess.entity.products.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.common.enums.Status;
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
