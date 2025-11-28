package com.kit.maximus.freshskinweb.dataaccess.specification;


import com.kit.maximus.freshskinweb.dataaccess.entity.skins.SkinTypeScoreRangeEntity;
import org.springframework.data.jpa.domain.Specification;
public class SkinTypeScoreRangeSpecifications {
    public static Specification<SkinTypeScoreRangeEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<SkinTypeScoreRangeEntity> hasSkinType(String skinType) {
        return (root, query, criteriaBuilder) -> {
            if (skinType == null || skinType.trim().isEmpty()) return null;
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("skinType").get("type")),
                    "%" + skinType.toLowerCase() + "%"
            );
        };
    }
}