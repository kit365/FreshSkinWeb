package com.kit.maximus.freshskinweb.repository;


import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.ProductVariantEntity;
import com.kit.maximus.freshskinweb.specification.ProductSpecification;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    Page<ProductEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<ProductEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    @EntityGraph(attributePaths = {"reviews"}) // Chỉ fetch các quan hệ cần thiết
    Optional<ProductEntity> findWithReviewsById(Long id);

    List<ProductEntity> findTop7ByStatusAndDeleted(Status status, boolean b, Sort discountPercent);

    ProductEntity findBySlug(String slug);

    List<ProductEntity> findTop3ByStatusAndDeletedAndFeatured(Status status, boolean b, boolean b1);


    Page<ProductEntity> findAllByCategoryIn(List<ProductCategoryEntity> subCategories, Pageable pageable);


//    List<ProductEntity> findByCategory_IdIn(List<Long> categoryIds);

    List<ProductEntity> findTop10ByCategory_IdIn(List<Long> ids);


    List<ProductEntity> findTop5ByTitleContaining(String request);

    List<ProductEntity> findAllByIdIn(List<Long> results);


//    @Query("SELECT p FROM ProductEntity p " +
//            "JOIN p.variants v " +
//            "WHERE v.price = :price")
//    Page<ProductEntity> findByProductVariantPrice(@Param("price") double price);
}
