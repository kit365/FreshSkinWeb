package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByUsername(String username);

    @Override
    Optional<UserEntity> findById(Long aLong);


    boolean existsByEmail(String email);
//    UserEntity findByUsername(String username);
    Optional<UserEntity> findAllByUsername(String username);
    @Query("SELECT s FROM UserEntity s WHERE s.username LIKE %:keyword%")
    List<UserEntity> searchByKeyword(@Param("keyword") String keyword);

    Optional<UserEntity> findByUsername(String username);


    UserEntity findByEmail(String email);


    Optional<UserEntity> findByProviderId(String providerId);
}
