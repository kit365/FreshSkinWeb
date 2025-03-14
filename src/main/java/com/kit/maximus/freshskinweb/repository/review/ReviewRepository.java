package com.kit.maximus.freshskinweb.repository.review;

import com.kit.maximus.freshskinweb.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByParentIsNull();
}
