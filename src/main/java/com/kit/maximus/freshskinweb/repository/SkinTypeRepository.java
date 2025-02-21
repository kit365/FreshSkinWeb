package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinTypeRepository extends JpaRepository<SkinTypeEntity, Long> {


}
