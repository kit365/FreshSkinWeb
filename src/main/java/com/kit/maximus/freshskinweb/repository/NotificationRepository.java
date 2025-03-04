package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
