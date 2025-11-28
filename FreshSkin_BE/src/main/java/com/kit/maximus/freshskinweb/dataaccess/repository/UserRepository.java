package com.kit.maximus.freshskinweb.dataaccess.repository;
import com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

   Optional<UserEntity> findByUsername(String username);
}
