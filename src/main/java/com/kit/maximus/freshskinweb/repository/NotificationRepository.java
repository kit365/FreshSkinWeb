package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {
    long countByIsRead(Boolean isRead); //đếm full

    List<NotificationEntity> findAllByOrderIsNull();

    long countByIsReadAndOrderIsNull(boolean b); //chỉ đếm order
}
