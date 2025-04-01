//package com.kit.maximus.freshskinweb.specification;
//
//import com.kit.maximus.freshskinweb.entity.DiscountEntity;
//import com.kit.maximus.freshskinweb.utils.DiscountType;
//import jakarta.persistence.criteria.Predicate;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DiscountSpecification {
//
//    public static Specification<DiscountEntity> filterByName(String name) {
//        return (root, query, criteriaBuilder) -> {
//            if (StringUtils.hasText(name)) {
//                System.out.println("Filtering by name: " + name); // Log kiểm tra
//                return criteriaBuilder.equal(root.get("name"), name);
//            }
//            return criteriaBuilder.conjunction();
//        };
//    }
//
//    public static Specification<DiscountEntity> filterByDiscountType(String discountType) {
//        return (root, query, criteriaBuilder) -> {
//            if (StringUtils.hasText(discountType)) {
//                try {
//                    DiscountType type = DiscountType.valueOf(discountType.toUpperCase());
//                    return criteriaBuilder.equal(root.get("discountType"), type);
//                } catch (IllegalArgumentException e) {
//                    return null; // Không lọc nếu type không hợp lệ
//                }
//            }
//            return criteriaBuilder.conjunction();
//        };
//    }
//
//    public static Specification<DiscountEntity> filterByIsGlobal(Boolean isGlobal) {
//        return (root, query, criteriaBuilder) -> {
//            System.out.println("Filtering by isGlobal: " + isGlobal); // Log kiểm tra
//            return isGlobal != null ? criteriaBuilder.equal(root.get("isGlobal"), isGlobal) : criteriaBuilder.conjunction();
//        };
//    }
//
//
//    public static Specification<DiscountEntity> sortByUpdatedAtAndUsed(Boolean sortByUsed) {
//        return (root, query, criteriaBuilder) -> {
//            if (sortByUsed) {
//                query.orderBy(
//                        criteriaBuilder.desc(root.get("used")),
//                        criteriaBuilder.desc(root.get("updatedAt"))
//                );
//            } else {
//                query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
//            }
//            return null; // Specification yêu cầu trả về Predicate, nhưng chỉ cần thêm sort thì có thể để null
//        };
//    }
//}
