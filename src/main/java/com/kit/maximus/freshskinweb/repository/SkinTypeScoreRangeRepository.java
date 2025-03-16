package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinTypeScoreRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinTypeScoreRangeRepository extends JpaRepository<SkinTypeScoreRangeEntity, Long>, JpaSpecificationExecutor<SkinTypeScoreRangeEntity> {
    @Query("SELECT s FROM SkinTypeScoreRangeEntity s " +
            "WHERE s.MinScore <= :score AND s.MaxScore >= :score " +
            "AND s.status = 'ACTIVE' AND s.deleted = false")
    Optional<SkinTypeScoreRangeEntity> findByScoreRange(@Param("score") Long score);
}
