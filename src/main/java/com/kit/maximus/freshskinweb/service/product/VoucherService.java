package com.kit.maximus.freshskinweb.service.product;

import com.kit.maximus.freshskinweb.dto.request.voucher.VoucherRequest;
import com.kit.maximus.freshskinweb.dto.response.VoucherResponse;
import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.VoucherMapper;
import com.kit.maximus.freshskinweb.repository.VoucherRepository;
import com.kit.maximus.freshskinweb.utils.DiscountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@Setter
@RequiredArgsConstructor
public class VoucherService {

    VoucherRepository voucherRepository;
    VoucherMapper voucherMapper;

    public boolean createVoucher(VoucherRequest voucherRequest) {
        boolean voucher = voucherRepository.existsByName(voucherRequest.getName());
        if(voucher) {
            throw new AppException(ErrorCode.VOUCHER_IS_EXISTED);
        }
        var mapVoucher = voucherMapper.toVoucherEntity(voucherRequest);
        voucherRepository.save(mapVoucher);
        log.info("Voucher Request: {}", voucherRequest);
        log.info("Mapped Voucher Entity: {}", mapVoucher);
        log.info("Start Date: {}", mapVoucher.getStartDate());
        log.info("Voucher Request: {}", voucherRequest);

        return true;
    }

    @Scheduled(cron = "0 0 0 * * TUE")
    public void autoCreateVoucher() {
        // Kiểm tra voucher tên TUESDAYVOUCHER đã tồn tại chưa
        String voucherName = "TUESDAYVOUCHER";
        VoucherRequest voucherRequest = new VoucherRequest();
        voucherRequest.setUsageLimit(100);
        voucherRequest.setMaxDiscount(BigDecimal.valueOf(50000));
        voucherRequest.setType(DiscountType.PERCENTAGE);
        voucherRequest.setMinOrderValue(BigDecimal.valueOf(200000));
        voucherRequest.setDiscountValue(BigDecimal.valueOf(10));

        LocalDate today = LocalDate.now();
        voucherRequest.setStartDate(java.sql.Date.valueOf(today));
        voucherRequest.setEndDate(java.sql.Date.valueOf(today.plusDays(7)));

        if (isVoucherNameExist(voucherName.toUpperCase())) {
            // Nếu voucher tồn tại, reset lại voucher với tên "TUESDAYVOUCHER"
            VoucherEntity existingVoucher = voucherRepository.findByName(voucherName).orElse(null);
            if (existingVoucher != null) {
                existingVoucher.setUsageLimit(100);
                existingVoucher.setMaxDiscount(BigDecimal.valueOf(50000));
                existingVoucher.setDiscountValue(BigDecimal.valueOf(10));
                existingVoucher.setStartDate(java.sql.Date.valueOf(today));
                existingVoucher.setEndDate(java.sql.Date.valueOf(today.plusDays(7)));
                voucherRepository.save(existingVoucher);
                log.info("Voucher '{}' đã được reset lại thành công!", voucherName);
            }
        } else {
            // Nếu voucher chưa tồn tại, tạo mới
            voucherRequest.setName(voucherName.toUpperCase());
            try {
                createVoucher(voucherRequest);
                log.info("Voucher '{}' tự động tạo thành công!", voucherRequest.getName());
            } catch (AppException e) {
                log.error("Lỗi khi tạo voucher: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Lỗi không xác định: ", e);
            }
        }
    }


    // Kiểm tra voucher có tên trùng không
    public boolean isVoucherNameExist(String name) {
        return voucherRepository.existsByName(name);
    }

//    // Tạo tên voucher duy nhất bằng cách thêm số vào tên
//    public String generateUniqueVoucherName(String baseName) {
//        int suffix = 1;
//        String uniqueName = baseName + suffix;
//        while (isVoucherNameExist(uniqueName)) {
//            suffix++;
//            uniqueName = baseName + suffix;
//        }
//        return uniqueName;
//    }


    public VoucherResponse getVoucher(String id) {
        System.out.println("Name: " + id);
        VoucherEntity voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return voucherMapper.toVoucherResponse(voucher);
    }

    public void updateVoucher(String id, VoucherRequest voucherRequest) {
        VoucherEntity voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        boolean nameExists = voucherRepository.existsByNameAndVoucherIdNot(voucherRequest.getName(), id);
        if (nameExists) {
            throw new AppException(ErrorCode.VOUCHER_IS_EXISTED);
        }

        voucherMapper.updateVoucher(voucher, voucherRequest);

        try {
            voucherRepository.save(voucher);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.VOUCHER_IS_EXISTED);
        }
    }

    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll().stream().map(voucherMapper::toVoucherResponse).collect(Collectors.toList());
    }

    public boolean deleteVoucher(String id) {
        VoucherEntity voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucherRepository.delete(voucher);
        return true;
    }

    public VoucherEntity validateVoucher(String name, BigDecimal totalPrice) {
        VoucherEntity voucher = voucherRepository.findByName(name).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        Date now = new Date();

        if (voucher.getStartDate().after(now)) {
            throw new AppException(ErrorCode.VOUCHER_NOT_VALID_YET);
        }

        if (voucher.getEndDate().before(now)) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        if (voucher.getUsed() >= voucher.getUsageLimit()) {
            throw new AppException(ErrorCode.VOUCHER_IS_USED_UP);
        }

        if (voucher.getMinOrderValue() != null
                && totalPrice.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new AppException(ErrorCode.ORDER_NOT_ELIGIBLE);
        }

        return voucher;
    }

    public BigDecimal applyVoucherDiscount(VoucherEntity voucher, BigDecimal orderTotal) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (voucher.getType() == DiscountType.PERCENTAGE) {
            discountAmount = orderTotal.multiply(voucher.getDiscountValue().divide(BigDecimal.valueOf(100)));

            // Nếu có giới hạn mức giảm, áp dụng mức tối đa
            if (voucher.getMaxDiscount() != null && discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
                discountAmount = voucher.getMaxDiscount();
            }
        } else if (voucher.getType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = voucher.getDiscountValue();
        }
        return orderTotal.subtract(discountAmount);
    }

    public List<VoucherResponse> getFourVoucher(){
        return voucherRepository.findTopFourPercentageVouchers().stream().map(voucherMapper::toVoucherResponse).collect(Collectors.toList());
    }
}
