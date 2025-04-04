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

    @Query(value = """
    SELECT DISTINCT p.* FROM product p
    INNER JOIN product_skin_type pst ON p.product_id = pst.product_id
    WHERE pst.skin_type_id = :skinTypeId
    AND p.deleted = false
    AND p.status = 'ACTIVE'
    ORDER BY p.position DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<ProductEntity> findAllActiveBySkinType(@Param("skinTypeId") Long skinTypeId, @Param("limit") int limit);
}
