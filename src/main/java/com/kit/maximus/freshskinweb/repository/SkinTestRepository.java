package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinTestRepository extends JpaRepository<SkinTestEntity, Long>, JpaSpecificationExecutor<SkinTestEntity> {
    SkinTestEntity findByUser(UserEntity user);
}
