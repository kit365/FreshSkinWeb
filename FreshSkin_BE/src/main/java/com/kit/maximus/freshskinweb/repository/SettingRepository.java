package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<SettingEntity, Long> {
    Optional<SettingEntity> findById(Long id);
}
