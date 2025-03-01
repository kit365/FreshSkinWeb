package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    boolean existsByTitle(String rolename);
//    boolean existsByID(Long RoleID);
//    Optional<RoleEntity> findAllByRolename(String rolename);
//    @Query("SELECT s FROM RoleEntity s WHERE s.roleName LIKE %:keyword%")
//    List<RoleEntity> searchByKeyword(@Param("keyword") String keyword);
//    Optional<RoleEntity> findByroleName(String roleName);
}
