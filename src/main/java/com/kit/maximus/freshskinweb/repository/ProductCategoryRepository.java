package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {


    Page<ProductCategoryEntity> findByTitleContainingIgnoreCaseAndDeletedAndParentIsNull(String keyword, boolean b, Pageable pageable);

    Page<ProductCategoryEntity> findByTitleContainingIgnoreCaseAndStatusAndDeletedAndParentIsNull(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<ProductCategoryEntity> findAllByDeletedAndParentIsNull(boolean b, Pageable pageable);


    Page<ProductCategoryEntity> findAllByStatusAndDeletedAndParentIsNull(Status statusEnum, boolean b, Pageable pageable);

    @Query("SELECT c FROM ProductCategoryEntity c WHERE c.parent.id IS NULL")
    List<ProductCategoryEntity> findAllParentCategories();


    List<ProductCategoryEntity> findAllByParentIsNull();
}
