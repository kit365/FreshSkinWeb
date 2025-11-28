package com.kit.maximus.freshskinweb.dataaccess.specification;
import com.kit.maximus.freshskinweb.common.enums.Status;
import com.kit.maximus.freshskinweb.common.enums.TypeUser;
import com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<UserEntity> hasRoleZero() {
        return (root, query, builder) ->
                builder.equal(root.get("role").get("id"), 0L);
    }

    public static Specification<UserEntity> filterByStatus(Status status) {
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }

    public static Specification<UserEntity> filterByType(TypeUser type) {
        return (root, query, builder) ->
                builder.equal(root.get("type"), type);
    }

    public static Specification<UserEntity> searchByKeyword(String keyword) {
        return (root, query, builder) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("firstName")), likePattern),
                    builder.like(builder.lower(root.get("lastName")), likePattern),
                    builder.like(root.get("phone"), likePattern)
            );
        };
    }
}
