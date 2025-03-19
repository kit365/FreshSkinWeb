package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {
    long countByIsRead(Boolean isRead); //đếm full


    long countByIsReadAndOrderIsNull(boolean b); //chỉ đếm order

    void deleteAllByIsReadAndOrderIsNull(boolean b);

    long countByIsReadAndReviewIsNull(boolean b);


    void deleteAllByIsReadAndReviewIsNull(boolean b);

    @EntityGraph(attributePaths = {"order.orderItems.productVariant.product.thumbnail"})
    List<NotificationEntity> findAllByReviewIsNull(Sort sort);

    @EntityGraph(attributePaths = {"review.product", "review.product.thumbnail"})
    List<NotificationEntity> findAllByOrderIsNull(Sort sort);

    @EntityGraph(attributePaths = {"review.product", "order.orderItems.productVariant.product.thumbnail"})
    List<NotificationEntity> findAllByOrderByIsReadAscTimeDesc();

    void deleteAllByIsRead(boolean b);
}
