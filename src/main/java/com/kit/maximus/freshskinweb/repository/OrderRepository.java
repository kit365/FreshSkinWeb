package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {

    Optional<OrderEntity> findById(String orderId);

    boolean existsByFirstName(String firstName);
    boolean existsByLastName(String lastName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);


//    OrderResponse findById(String orderId);
}
