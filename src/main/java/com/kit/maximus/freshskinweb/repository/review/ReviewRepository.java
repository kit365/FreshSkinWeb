package com.kit.maximus.freshskinweb.repository.review;

import com.kit.maximus.freshskinweb.entity.ReviewEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByParentIsNull();


    Page<ReviewEntity> findAllByParentIsNullAndStatusAndDeleted(Pageable pageable, Status active, boolean b);


    Page<ReviewEntity> findAllByParentIsNullAndStatusAndDeletedAndProduct_Id(Pageable pageable, Status status, boolean b, long id);


//    Integer countAllByParentIsNullAndProduct_Id(Long productId);
//
//    Integer countAllByParentIsNullAndProduct_IdAndRating(Long productId, int rating);

    @EntityGraph(attributePaths = {"product"})
    Integer countAllByParentIsNullAndProduct_Id(Long productId);

    @EntityGraph(attributePaths = {"product"})
    Integer countAllByParentIsNullAndProduct_IdAndRating(Long productId, int rating);
}
