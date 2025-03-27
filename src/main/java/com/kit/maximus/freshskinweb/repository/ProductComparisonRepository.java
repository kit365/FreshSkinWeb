package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductComparisonEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductComparisonRepository extends JpaRepository<ProductComparisonEntity, Long> {
    ProductComparisonEntity findByUser(UserEntity user);
}
