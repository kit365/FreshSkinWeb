package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity,Long>    {
}
