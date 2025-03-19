package com.kit.maximus.freshskinweb.repository.review;

import com.kit.maximus.freshskinweb.entity.review.ReviewEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByParentIsNull();


    Page<ReviewEntity> findAllByParentIsNullAndStatusAndDeleted(Pageable pageable, Status active, boolean b);


    Page<ReviewEntity> findAllByParentIsNullAndStatusAndDeletedAndProduct_Id(Pageable pageable, Status status, boolean b, long id);


    Integer countAllByParentIsNullAndProduct_Id(Long productId);
}
