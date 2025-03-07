package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductCategorySpecification {

    public static Specification<ProductCategoryEntity> findCategoryByTitle(List<String> titles) {
        if (titles.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            Join<ProductCategoryEntity, ProductCategoryEntity> parentJoin = root.join("parent", JoinType.LEFT);
            Join<ProductCategoryEntity, ProductCategoryEntity> grandParentJoin = parentJoin.join("parent", JoinType.LEFT);

            //coalesce sẽ lựa chọn 1 trong 2 giá trị trong list(" hoặc có giá trị)
            //literal như where = literalvalue
            return criteriaBuilder.or(
                    criteriaBuilder.coalesce(root.get("title"), criteriaBuilder.literal("")).in(titles),
                    criteriaBuilder.coalesce(parentJoin.get("title"), criteriaBuilder.literal("")).in(titles),
                    criteriaBuilder.coalesce(grandParentJoin.get("title"), criteriaBuilder.literal("")).in(titles)
            );
        };
    }



    public static Specification<ProductCategoryEntity> filterByStatus(Status status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<ProductCategoryEntity> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }


}
