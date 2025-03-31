package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {

    Optional<VoucherEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndVoucherIdNot(String name, String voucherId);

    @Query(value = "SELECT * FROM vouchers WHERE type = 'PERCENTAGE' ORDER BY usage_limit DESC LIMIT 4", nativeQuery = true)
    List<VoucherEntity> findTop4PercentageVouchers();

}
