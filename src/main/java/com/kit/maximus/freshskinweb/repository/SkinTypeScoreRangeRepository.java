package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinTypeScoreRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SkinTypeScoreRangeRepository extends JpaRepository<SkinTypeScoreRangeEntity, Long>, JpaSpecificationExecutor<SkinTypeScoreRangeEntity> {
    @Query(value = """
    SELECT * FROM skin_type_score_range 
    WHERE CAST(:score AS DECIMAL) <= CAST(max_score AS DECIMAL)
    AND CAST(:score AS DECIMAL) >= CAST(min_score AS DECIMAL)
    """, nativeQuery = true)
    @Transactional(readOnly = true)
    Optional<SkinTypeScoreRangeEntity> findByScoreRange(@Param("score") Long score);
}
