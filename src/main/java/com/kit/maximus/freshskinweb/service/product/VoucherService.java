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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    public VoucherResponse getVoucher(String id) {
        System.out.println("Name: " + id);
        VoucherEntity voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return voucherMapper.toVoucherResponse(voucher);
    }

    public void updateVoucher(String id, VoucherRequest voucherRequest) {
        VoucherEntity voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucherMapper.updateVoucher(voucher, voucherRequest);
        voucherRepository.save(voucher);
    }

    public List<VoucherEntity> getAllVouchers() {
        return voucherRepository.findAll();
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


}
