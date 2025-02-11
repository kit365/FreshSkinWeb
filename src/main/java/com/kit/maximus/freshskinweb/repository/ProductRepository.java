package com.kit.maximus.freshskinweb.repository;


import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findAllByStatus(Status status, Pageable pageable);

    List<ProductEntity> findByTitleLike(String title);



    Page<ProductEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ProductEntity> findByTitleContainingIgnoreCaseAndStatus(String keyword, Status statusEnum, Pageable pageable);
}
