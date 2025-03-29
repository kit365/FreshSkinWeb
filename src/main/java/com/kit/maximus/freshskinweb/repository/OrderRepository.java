package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.utils.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {

    Optional<OrderEntity> findById(String orderId);

    List<OrderEntity> findAllByPhoneNumber(String phoneNumber);

    List<OrderEntity> findAllByEmail(String email);

    List<OrderEntity> findAllByEmailAndPhoneNumber(String email, String phoneNumber, Sort sort);

    boolean existsByFirstName(String firstName);

    boolean existsByLastName(String lastName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

//    @Query("SELECT o FROM OrderEntity o " +
//            "LEFT JOIN FETCH o.orderItems items " +
//            "LEFT JOIN FETCH o.user u " +
//            "LEFT JOIN FETCH items.productVariant variant " +
//            "LEFT JOIN FETCH variant.product " +
//            "WHERE o.orderId = :orderId")
//    Optional<OrderEntity> findByOrderIdWithDetails(@Param("orderId") String orderId);

    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderItems items " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH items.productVariant variant " +
            "LEFT JOIN FETCH variant.product " +
            "WHERE o.orderId = :orderId")
    Optional<OrderEntity> findByOrderIdWithDetails(@Param("orderId") String orderId);

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT SUM(o.totalPrice) FROM OrderEntity o WHERE o.orderStatus = :orderStatus")
    BigDecimal sumTotalPriceByOrderStatus(@Param("orderStatus") OrderStatus orderStatus);


    @Query("SELECT SUM(x.totalPrice), Date(x.createdAt) FROM OrderEntity x " +
            "WHERE x.orderStatus = :orderStatus GROUP BY Date(x.createdAt)")
    List<Object[]> getTotalPriceByDate(@Param("orderStatus") OrderStatus orderStatus);


//    OrderResponse findById(String orderId);
}
