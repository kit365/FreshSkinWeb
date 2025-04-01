package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductComparisonEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductComparisonRepository extends JpaRepository<ProductComparisonEntity, Long> {
    ProductComparisonEntity findByUser(UserEntity user);

    @Query("SELECT p FROM ProductComparisonEntity p JOIN FETCH p.products WHERE p.id = :id")
    Optional<ProductComparisonEntity> findByIdWithProducts(@Param("id") Long id);

}
