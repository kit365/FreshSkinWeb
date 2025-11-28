package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

}
