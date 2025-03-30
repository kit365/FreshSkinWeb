package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.voucher.VoucherRequest;
import com.kit.maximus.freshskinweb.dto.response.VoucherResponse;
import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherEntity toVoucherEntity(VoucherRequest voucherRequest);

    VoucherResponse toVoucherResponse(VoucherEntity voucherEntity);

    void updateVoucher(@MappingTarget VoucherEntity voucherEntity, VoucherRequest voucherRequest);

}
