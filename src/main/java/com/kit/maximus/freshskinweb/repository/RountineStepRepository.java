package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.RountineStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RountineStepRepository extends JpaRepository<RountineStepEntity, Long> {

    @Query("SELECT MAX(r.position) FROM RountineStepEntity r")
    Optional<Integer> findMaxPosition();
}
