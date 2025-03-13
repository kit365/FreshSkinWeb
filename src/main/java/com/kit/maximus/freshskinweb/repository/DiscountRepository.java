package com.kit.maximus.freshskinweb.repository;


import com.kit.maximus.freshskinweb.entity.DiscountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, String>, JpaSpecificationExecutor<DiscountEntity> {
    Page<DiscountEntity> findAll(Specification<DiscountEntity> spec, Pageable pageable);

    boolean existsByDiscountId(String discountId);

    boolean existsByName(String name);

}
