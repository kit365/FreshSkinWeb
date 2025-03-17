package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.voucher.VoucherRequest;
import com.kit.maximus.freshskinweb.dto.response.VoucherResponse;
import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.VoucherMapper;
import com.kit.maximus.freshskinweb.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public VoucherResponse getVoucher(String name) {
        System.out.println("Name: " + name);
        VoucherEntity voucher = voucherRepository.findByName(name).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return voucherMapper.toVoucherResponse(voucher);
    }

    public void updateVoucher(String name, VoucherRequest voucherRequest) {
        VoucherEntity voucher = voucherRepository.findByName(name).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucherMapper.updateVoucher(voucher, voucherRequest);
        voucherRepository.save(voucher);
    }

    public List<VoucherEntity> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public boolean deleteVoucher(String name) {
        VoucherEntity voucher = voucherRepository.findByName(name).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucherRepository.delete(voucher);
        return true;
    }


}
