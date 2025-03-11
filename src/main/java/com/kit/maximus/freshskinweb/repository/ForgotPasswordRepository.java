package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.ForgotPasswordEntity;
import com.kit.maximus.freshskinweb.utils.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordEntity, Long> {
    List<ForgotPasswordEntity> findByEmailAndStatus(String email, Status status);
    Optional<ForgotPasswordEntity> findByEmailAndOTPAndStatus(String email, String otp, Status status);
}