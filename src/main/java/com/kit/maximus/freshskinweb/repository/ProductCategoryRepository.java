package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {


    @Query("SELECT c FROM ProductCategoryEntity c WHERE c.parent.id IS NULL")
    List<ProductCategoryEntity> findAllParentCategories();


    List<ProductCategoryEntity> findAllByParentIsNull();


    Page<ProductCategoryEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<ProductCategoryEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<ProductCategoryEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    Page<ProductCategoryEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    List<ProductCategoryEntity> findAllByStatusAndDeletedAndFeatured(Status status, boolean b, boolean b1, Sort position);

    List<ProductCategoryEntity> findTop8ByStatusAndDeletedAndFeatured(Status status, boolean deleted, boolean featured, Sort position);

    List<ProductCategoryEntity> findTop2ByStatusAndDeletedAndFeatured(Status status, boolean deleted, boolean featured, Sort position);
}
