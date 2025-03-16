package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {

    Optional<OrderEntity> findById(String orderId);

    // Hỗ trợ cho gửi mail cho order
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderItems items " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH items.productVariant variant " +
            "LEFT JOIN FETCH variant.product " +
            "WHERE o.orderId = :orderId")
    Optional<OrderEntity> findByOrderIdWithDetails(@Param("orderId") String orderId);

    // Hỗ trợ query cho phân trang
    @Query(value = """
    SELECT o.* FROM `order` o
    WHERE (:status IS NULL OR o.status = :status)
    AND (:keyword IS NULL OR
        (LOWER(o.first_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
         LOWER(o.last_name) LIKE LOWER(CONCAT('%', :keyword, '%'))))
    AND (:orderId IS NULL OR o.order_id = :orderId)
    AND o.deleted = false
    ORDER BY
    CASE 
        WHEN :priorityStatus IS NOT NULL AND o.order_status = :priorityStatus THEN 0
        WHEN o.order_status = 'PENDING' THEN 1
        WHEN o.order_status = 'CANCELED' THEN 2
        WHEN o.order_status = 'COMPLETED' THEN 3
        ELSE 4
    END,
    o.updated_at DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM `order` o
    WHERE (:status IS NULL OR o.status = :status)
    AND (:keyword IS NULL OR
        (LOWER(o.first_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
         LOWER(o.last_name) LIKE LOWER(CONCAT('%', :keyword, '%'))))
    AND (:orderId IS NULL OR o.order_id = :orderId)
    AND o.deleted = false
    """,
            nativeQuery = true)
    Page<OrderEntity> findAllOrdersWithSort(
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("orderId") String orderId,
            @Param("priorityStatus") String priorityStatus,
            Pageable pageable);

    @Query(value = """
    SELECT o.* FROM `order` o
    WHERE (:status IS NULL OR o.status = :status)
    AND (:keyword IS NULL OR
        (LOWER(o.first_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
         LOWER(o.last_name) LIKE LOWER(CONCAT('%', :keyword, '%'))))
    AND (:orderId IS NULL OR o.order_id = :orderId)
    AND o.deleted = false
    ORDER BY o.updated_at DESC
    """,
            nativeQuery = true)
    Page<OrderEntity> findAllOrdersByUpdatedAt(
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("orderId") String orderId,
            Pageable pageable);
}
