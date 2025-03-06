package com.kit.maximus.freshskinweb.specification;

import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.utils.SkinType;
import com.kit.maximus.freshskinweb.utils.Status;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ProductSpecification {

    public static Specification<ProductEntity> filterByKeyword(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }


    public static Specification<ProductEntity> filterByStatus(Status status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<ProductEntity> sortByPrice(Sort.Direction sortDirection) {
        return (root, query, criteriaBuilder) -> {

            Join<ProductEntity, ProductVariantEntity> productVariantJoin = root.join("variants", JoinType.LEFT);

            // Tính giá trị min price của product_variant (group by product)
            Expression<Double> minPrice = criteriaBuilder.min(productVariantJoin.get("price"));

            // Group by product để tính toán min price cho mỗi sản phẩm
            query.groupBy(root.get("id"));

            query.orderBy(
                    sortDirection == Sort.Direction.ASC ? criteriaBuilder.asc(minPrice) :
                            criteriaBuilder.desc(minPrice)
            );

            return query.getRestriction();
        };

        //sql : SELECT pe1.product_id, pe1.benefits, pe1.brandid, pe1.created_at, pe1.deleted, pe1.description,
        //       pe1.discount_percentage, pe1.featured, pe1.ingredients, pe1.origin, pe1.position,
        //       pe1.skin_issues, pe1.slug, pe1.status, pe1.title, pe1.update_at, pe1.usage_instructions
        //FROM product pe1
        //LEFT JOIN product_variant v1 ON pe1.product_id = v1.productid
        //WHERE pe1.deleted = ?
        //GROUP BY pe1.product_id
        //ORDER BY MIN(v1.price) DESC
        //LIMIT ?, ?;
    }


    public static Specification<ProductEntity> sortByPosition(Sort.Direction sortDirection) {
        return (root, query, criteriaBuilder) -> {

            query.orderBy(sortDirection == Sort.Direction.ASC ? criteriaBuilder.asc(root.get("position")) : criteriaBuilder.desc(root.get("position")));
            return query.getRestriction();
        };
    }

    public static Specification<ProductEntity> sortByTitle(Sort.Direction sortDirection) {
        return (root, query, criteriaBuilder) -> {

            query.orderBy(sortDirection == Sort.Direction.ASC ? criteriaBuilder.asc(root.get("title")) : criteriaBuilder.desc(root.get("title")));
            return query.getRestriction();
        };
    }

    public static Specification<ProductEntity> isNotDeleted() {
        return (root, query, builder) -> builder.equal(root.get("deleted"), false);
    }

    public static Specification<ProductEntity> isDeleted() {
        return (root, query, builder) -> builder.equal(root.get("deleted"), true);
    }


    public static Specification<ProductEntity> findByParentCategorySlug(String slug) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<ProductEntity, ProductCategoryEntity> productCategory = root.join("category", JoinType.LEFT);
            Join<ProductCategoryEntity, ProductCategoryEntity> parentCategory = productCategory.join("parent", JoinType.LEFT);
            Join<ProductCategoryEntity, ProductCategoryEntity> grandParentCategory = parentCategory.join("parent", JoinType.LEFT);

            if (slug.equals("san-pham-moi")) {
                query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.now().minusWeeks(2));
            }

            return criteriaBuilder.or(
                    criteriaBuilder.equal(productCategory.get("slug"), slug),
                    criteriaBuilder.equal(parentCategory.get("slug"), slug),
                    criteriaBuilder.equal(grandParentCategory.get("slug"), slug)
            );
        };
    }


    public static Specification<ProductEntity> findByBrandSlug(String slug) {
        if(slug.isBlank()){
            return null;
        }

        if(slug.equals("thuong-hieu")) {
            return (root, query, criteriaBuilder) -> {
                return criteriaBuilder.conjunction();
            };
        }


        return (root, query, criteriaBuilder) -> {
           return  criteriaBuilder.equal(root.get("brand").get("slug"), slug);
        };
    }


    public static Specification<ProductEntity> filterByCategory(List<String> categoryNames) {
        if (categoryNames.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            Join<ProductEntity, ProductCategoryEntity> product_category = root.join("category");
            return product_category.get("title").in(categoryNames);
        };

        //SELECT * FROM Product p
        //JOIN Product_Category pc ON p.ProductId = pc.productID
        //JOIN ProductCategory c ON pc.categoryID = c.categoryID
        //WHERE c.title IN ('Category1', 'Category2', 'Category3');
    }

    public static Specification<ProductEntity> filterByBrand(List<String> brandNames) {
        if (brandNames.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> root.get("brand").get("title").in(brandNames));

        //SELECT * FROM Product p
        //WHERE p.brandID IN (
        //    SELECT brandID FROM ProductBrand pb WHERE pb.title IN ('Brand1', 'Brand2', 'Brand3')
        //);
    }

    public static Specification<ProductEntity> filterBySkinType(List<String> skinTypeNames) {
        if (skinTypeNames.isEmpty()) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> {
            Join<ProductEntity, SkinTypeEntity> product_skinType = root.join("skinTypes");
            return product_skinType.get("type").in(skinTypeNames);
        });
    }

    public static Specification<ProductEntity> filterByPrice(Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null || minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            Join<ProductEntity, ProductVariantEntity> productVariantJoin = root.join("variants", JoinType.LEFT);

            Expression<Double> minVariantPrice = criteriaBuilder.min(productVariantJoin.get("price"));

            //đảm bảo mỗi sản phẩm chỉ có 1 giá min duy nhất
            query.groupBy(root.get("id"));

            if (minPrice.equals(maxPrice)) {
                return criteriaBuilder.equal(minVariantPrice, minPrice);
            } else if (maxPrice < 100000) {
                return criteriaBuilder.lessThan(minVariantPrice, maxPrice);
            } else if (maxPrice >= 100000 && maxPrice <= 700000) {
                return criteriaBuilder.between(minVariantPrice, minPrice, maxPrice);
            }
            return criteriaBuilder.greaterThanOrEqualTo(minVariantPrice, minPrice);
        };
    }



    public static Specification<ProductEntity> sortByPrice(String sortDirection) {
        return (root, query, criteriaBuilder) -> {
            return null;
        };
    }


}
