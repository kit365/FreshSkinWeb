package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ProductBrandEntity;
import com.kit.maximus.freshskinweb.service.ProductService;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrandEntity,Long> {
    Page<ProductBrandEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    Page<ProductBrandEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<ProductBrandEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<ProductBrandEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    ProductBrandEntity findBySlug(String slug);
}
