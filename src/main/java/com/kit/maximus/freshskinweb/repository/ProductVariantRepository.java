package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long> {
    List<ProductVariantEntity> findAllByProduct_Id(Long productId);

//    Object findAllById(Long id);
}
