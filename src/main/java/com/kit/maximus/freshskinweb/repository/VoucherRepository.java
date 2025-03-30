package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {

    Optional<VoucherEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndVoucherIdNot(String name, String voucherId);
}
