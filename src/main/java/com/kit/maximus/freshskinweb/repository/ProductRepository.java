package com.kit.maximus.freshskinweb.repository;


import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {


    @EntityGraph(attributePaths = {"reviews"}) // Chỉ fetch các quan hệ cần thiết
    Optional<ProductEntity> findWithReviewsById(Long id);

//    List<ProductEntity> findTop7ByStatusAndDeleted(Status status, boolean b, Sort discountPercent);

    @Query("SELECT p.id FROM ProductEntity p WHERE p.status = :status AND p.deleted = :deleted ORDER BY p.discountPercent DESC")
    List<Long> findTop7ProductIdsByStatusAndDeleted(@Param("status") Status status, @Param("deleted") boolean deleted, Pageable pageable);


    @Query("SELECT p.id FROM ProductEntity p WHERE p.status = :status AND p.deleted = :deleted ORDER BY p.featured DESC")
    List<Long> findTop3ByStatusAndDeletedAndFeatured(@Param("status") Status status, @Param("deleted") boolean deleted, Pageable pageable);

    List<ProductEntity> findAllByIdIn(List<Long> ids);

//    List<ProductEntity> findTop3ByStatusAndDeletedAndFeatured(Status status, boolean b, boolean b1);



//    @Query("SELECT p FROM ProductEntity p " +
//            "JOIN p.variants pv " +
//            "JOIN OrderItemEntity oi ON oi.productVariant.id = pv.id " +
//            "GROUP BY p.id " +
//            "ORDER BY COUNT(oi) DESC")
//    List<ProductEntity> findTop10SellingProducts(Pageable pageable);

    @Query("SELECT p.id FROM ProductEntity p " +
            "JOIN p.variants pv " +
            "JOIN OrderItemEntity oi ON oi.productVariant.id = pv.id " +
            "WHERE p.status = 'ACTIVE' AND p.deleted = false " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(oi) DESC")
    List<Long> findTop10SellingProducts(Pageable pageable);

    @Query("SELECT p.id, p.title, COUNT(oi) " +
            "FROM ProductEntity p " +
            "JOIN p.variants pv " +
            "JOIN OrderItemEntity oi ON oi.productVariant.id = pv.id " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(oi) DESC")
    List<Object[]> findTop10SellingProductsDashBoard(Pageable pageable);

    long countByStatusAndDeleted(Status status, boolean b);

    // Truy vấn lấy danh sách sản phẩm theo skinTypeId và categoryKeyword và chọn theo top 5 lượt mua nhiều nhất
    @Query("SELECT DISTINCT p.id FROM ProductEntity p " +
            "JOIN p.skinTypes st " +
            "JOIN p.category c " +
            "JOIN p.variants pv " +
            "JOIN OrderItemEntity oi ON oi.productVariant.id = pv.id " +
            "WHERE st.id = :skinTypeId " +
            "AND LOWER(c.title) LIKE LOWER(CONCAT('%', :categoryKeyword, '%')) " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deleted = false " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(oi) DESC " +
            "LIMIT 5")
    List<Long> findTop5SellingProductsBySkinTypeAndCategory(
            @Param("skinTypeId") Long skinTypeId,
            @Param("categoryKeyword") String categoryKeyword
    );

    // Tìm sản phẩm có danh mục con được bán nhiều nhất
    @Query("SELECT c.id " +
            "FROM ProductCategoryEntity c " +
            "JOIN c.products p " +
            "JOIN p.variants pv " +
            "JOIN OrderItemEntity oi ON oi.productVariant.id = pv.id " +
            "WHERE c.parent.id = :parentCategoryId " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deleted = FALSE " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(oi) DESC")
    List<Long> findTop5BestSellingChildCategoryIdByParent(@Param("parentCategoryId") Long parentCategoryId, Pageable pageable);

    // Nếu danh mục cha - con không có trong sản phẩm được order nhiều nhất, thì dựa theo top 5 position của sp đó
    @Query("SELECT p.id " +
            "FROM ProductEntity p " +
            "JOIN p.categories c " +
            "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :categoryTitle, '%')) " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deleted = FALSE " +
            "ORDER BY p.position ASC")
    List<Long> findTop5ProductsByCategoryOrderedByPosition(@Param("categoryTitle") String categoryTitle, Pageable pageable);

}
