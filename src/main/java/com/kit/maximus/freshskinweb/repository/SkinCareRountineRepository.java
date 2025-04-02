package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinCareRountineRepository  extends JpaRepository<SkinCareRoutineEntity, Long>, JpaSpecificationExecutor<SkinCareRoutineEntity> {

    SkinCareRoutineEntity findBySkinType(SkinTypeEntity skinType);
}
