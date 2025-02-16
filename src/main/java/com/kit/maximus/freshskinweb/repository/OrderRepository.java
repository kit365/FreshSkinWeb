package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Override
    Optional<OrderEntity> findById(Long orderId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
