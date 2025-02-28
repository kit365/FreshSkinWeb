package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategoryEntity, Long> {
    Page<BlogCategoryEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    Page<BlogCategoryEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<BlogCategoryEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<BlogCategoryEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    List<BlogCategoryEntity> findTop4ByStatusAndDeletedAndFeatured(Status status, boolean deleted, boolean featured, Sort position);

    BlogEntity findBlogBySlug(String slug);
}
