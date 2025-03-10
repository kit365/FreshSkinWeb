package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DiscountSpecification {

    public static Specification<DiscountEntity> filterByPromoCode(String promoCode) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(promoCode) ?
                        criteriaBuilder.like(root.get("PromoCode"), "%" + promoCode + "%") : null;
    }

    public static Specification<DiscountEntity> filterByDiscountType(String discountType) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(discountType)) {
                try {
                    DiscountType type = DiscountType.valueOf(discountType.toUpperCase());
                    return criteriaBuilder.equal(root.get("DiscountType"), type);
                } catch (IllegalArgumentException e) {
                    return null; // Trả về null nếu discountType không hợp lệ
                }
            }
            return null;
        };
    }

    public static Specification<DiscountEntity> filterByIsGlobal(Boolean isGlobal) {
        return (root, query, criteriaBuilder) ->
                isGlobal != null ? criteriaBuilder.equal(root.get("isGlobal"), isGlobal) : null;
    }

    public static Specification<DiscountEntity> sortByUpdatedAtAndUsed(Boolean sortByUsed) {
        return (root, query, criteriaBuilder) -> {
            if (sortByUsed) {
                query.orderBy(
                        criteriaBuilder.desc(root.get("Used")),
                        criteriaBuilder.desc(root.get("updatedAt"))
                );
            } else {
                query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            }
            return null; // Specification yêu cầu trả về Predicate, nhưng chỉ cần thêm sort thì có thể để null
        };
    }
}
