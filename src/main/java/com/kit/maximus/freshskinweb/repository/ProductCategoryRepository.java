package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long>, JpaSpecificationExecutor<ProductCategoryEntity> {


    @Query("SELECT c FROM ProductCategoryEntity c WHERE c.parent.id IS NULL")
    List<ProductCategoryEntity> findAllParentCategories();

    @Query("SELECT c FROM ProductCategoryEntity c WHERE c.slug = :slug")
    ProductCategoryEntity findCategoryBySlug(@Param("slug") String slug);


    List<ProductCategoryEntity> findAllByParentIsNull();


    Page<ProductCategoryEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<ProductCategoryEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<ProductCategoryEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    Page<ProductCategoryEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    List<ProductCategoryEntity> findTop8ByStatusAndDeletedAndFeatured(Status status, boolean deleted, boolean featured, Sort position);


    List<ProductCategoryEntity> findAllByStatusAndDeleted(Status status, boolean b);



    List<ProductCategoryEntity> findAllByStatusAndDeletedAndTitleContainingIgnoreCase(Status status, boolean b, String title);

    Page<ProductCategoryEntity> findAllByStatusAndDeletedAndId(Status status, boolean b, Pageable pageable, Long id);

    List<ProductCategoryEntity> findByParentId(Long parentId);

    List<ProductCategoryEntity> findAllByParentId(Long id);
    Page<ProductCategoryEntity> findAllByParentId(Long id, Pageable pageable);

    ProductCategoryEntity findBySlug(String slug);

    @Query("SELECT pc FROM ProductCategoryEntity pc " +
            "LEFT JOIN pc.products p " +
            "GROUP BY pc " +
            "ORDER BY COUNT(p) DESC")
    List<ProductCategoryEntity> findTop5CategoriesWithMostProducts(Pageable pageable);
}
