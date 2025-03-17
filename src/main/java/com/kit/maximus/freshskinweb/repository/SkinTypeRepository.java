package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.utils.SkinType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinTypeRepository extends JpaRepository<SkinTypeEntity, Long> {
    @Override
    @NotNull
    Optional<SkinTypeEntity> findById(Long aLong);

    SkinTypeEntity findByType(String type);

    boolean existsByTypeContainingIgnoreCase(String type);

}
