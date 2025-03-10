package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.UserDiscountUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDiscountUsageRepository extends JpaRepository<UserDiscountUsageEntity, Long> {
}
