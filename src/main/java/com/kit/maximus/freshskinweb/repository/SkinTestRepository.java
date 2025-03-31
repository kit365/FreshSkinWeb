package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinTestEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkinTestRepository extends JpaRepository<SkinTestEntity, Long>, JpaSpecificationExecutor<SkinTestEntity> {
    SkinTestEntity findByUser(UserEntity user);

    @Query("SELECT st.skinType.type as skinType, COUNT(st) as count " +
            "FROM SkinTestEntity st " +
            "GROUP BY st.skinType.type")
    List<Object[]> countBySkinTypes();
}
