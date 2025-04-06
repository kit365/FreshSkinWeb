//package com.kit.maximus.freshskinweb.specification;
//
//import com.kit.maximus.freshskinweb.entity.SearchKeywordEntity;
//import org.springframework.data.jpa.domain.Specification;
//
//public class KeywordSpecification {
//
//    public static Specification<SearchKeywordEntity> topKeywords() {
//        return (root, query, criteriaBuilder) -> {
//            query.orderBy(criteriaBuilder.desc(root.get("count")));
//            return criteriaBuilder.conjunction();
//        };
//    }
//}
