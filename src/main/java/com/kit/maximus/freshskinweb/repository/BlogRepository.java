package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.BlogEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogEntity, Long> {

    List<BlogEntity> findAllByIdInAndStatus(List<Long> id, Status status);

    Page<BlogEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    Page<BlogEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<BlogEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<BlogEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);
}
