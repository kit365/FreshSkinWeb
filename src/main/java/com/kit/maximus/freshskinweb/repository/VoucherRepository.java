package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {

    Optional<VoucherEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndVoucherIdNot(String name, String voucherId);

    // Truy vấn native: Lọc các voucher loại PERCENTAGE còn hạn sử dụng, sắp xếp theo usage_limit giảm dần, lấy 4 cái đầu
    @Query(value = "SELECT * FROM vouchers WHERE type = 'PERCENTAGE' AND end_date > :currentDate ORDER BY usage_limit DESC LIMIT 4",
            nativeQuery = true)
    List<VoucherEntity> findTopFourPercentageVouchers(Date currentDate);

    // Hàm tìm voucher còn hạn sử dụng (sử dụng endDate)
    @Query("SELECT v FROM VoucherEntity v WHERE v.endDate > :currentDate")
    List<VoucherEntity> findValidVouchers(Date currentDate);
}
