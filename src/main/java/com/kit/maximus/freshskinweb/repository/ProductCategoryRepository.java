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

import java.time.LocalDateTime;
import java.util.Collection;
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


    @Query("SELECT pc.title, COUNT(p) FROM ProductCategoryEntity pc " +
            "LEFT JOIN pc.products p " +
            "GROUP BY pc.title " +
            "ORDER BY COUNT(p) DESC")
    List<Object[]> findTop5CategoriesWithProductCount(Pageable pageable);


    @Query(value = """
            
                   SELECT
               DATE(o.created_at) AS order_date,
                c.title AS category_name,
               SUM(o.total_price) AS total_revenue
              FROM `order` o
               JOIN order_item oi ON o.order_id = oi.order_id
               JOIN product_variant pv ON oi.product_variant_id = pv.product_variant_id
            JOIN product p ON pv.productid = p.product_id
            JOIN product_category pc ON p.product_id = pc.productID
            JOIN category c ON pc.categoryID = c.id
            WHERE o.order_status = 'COMPLETED'
             GROUP BY DATE(o.created_at), c.title
               ORDER BY order_date DESC, total_revenue DESC;
            """, nativeQuery = true)
    List<Object[]> findCategoriesRevenueGroupByDate();
}
