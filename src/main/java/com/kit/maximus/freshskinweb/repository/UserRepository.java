package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
    Optional<UserEntity> findAllByUsername(String username);

    boolean existsByUsername(String username);
}
