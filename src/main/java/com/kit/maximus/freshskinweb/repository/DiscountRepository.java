package com.kit.maximus.freshskinweb.repository;


import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, String>, JpaSpecificationExecutor<DiscountEntity> {
    Page<DiscountEntity> findAll(Specification<DiscountEntity> spec, Pageable pageable);

    boolean existsByDiscountId(String discountId);

    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE DiscountEntity d SET d.used = d.used + :count " +
            "WHERE d.discountId = :id AND (d.usageLimit IS NULL OR d.used + :count <= d.usageLimit)")
    int incrementUsage(@Param("id") String id, @Param("count") int count);


}
