package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    void deleteByName(String name);

}
