package com.kit.maximus.freshskinweb.repository;


import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByIdInAndStatus(List<Long> id, Status status);

    Page<ProductEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    Page<ProductEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<ProductEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<ProductEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    List<ProductEntity> findTop7ByStatusAndDeleted(Status status, boolean b, Sort discountPercent);

    ProductEntity findBySlug(String slug);


//    @Query("SELECT p FROM ProductEntity p " +
//            "JOIN p.variants v " +
//            "WHERE v.price = :price")
//    Page<ProductEntity> findByProductVariantPrice(@Param("price") double price);
}
