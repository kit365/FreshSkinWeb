package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.voucher.VoucherRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.VoucherResponse;
import com.kit.maximus.freshskinweb.entity.VoucherEntity;
import com.kit.maximus.freshskinweb.service.product.VoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vouchers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherController {

    VoucherService voucherService;

    @PostMapping("/create")
    public ResponseAPI<VoucherResponse> createVoucher(@RequestBody VoucherRequest voucherRequest) {
        System.out.println("Start Date: " + voucherRequest.getStartDate()); // In ra để kiểm tra
        System.out.println("End Date: " + voucherRequest.getEndDate());
        String message = "Tạo voucher thành công";
        voucherService.createVoucher(voucherRequest);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/search/{name}")
    public ResponseAPI<VoucherResponse> getVoucher(@PathVariable String name) {
        String message = "Lấy voucher thành công";
        VoucherResponse voucherResponse = voucherService.getVoucher(name);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).data(voucherResponse).build();
    }

    @PatchMapping("/update/{name}")
    public ResponseAPI<VoucherResponse> updateVoucher(@PathVariable String name, @RequestBody VoucherRequest voucherRequest) {
        String message = "Cập nhật voucher thành công";
        voucherService.updateVoucher(name, voucherRequest);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<VoucherEntity>> getAllVouchers() {
        String message = "Lấy danh sách voucher thành công";
        return ResponseAPI.<List<VoucherEntity>>builder().code(HttpStatus.OK.value()).message(message).data(voucherService.getAllVouchers()).build();
    }

    @DeleteMapping("/delete/{name}")
    public ResponseAPI<VoucherResponse> deleteVoucher(@PathVariable String name) {
        String message = "Xóa voucher thành công";
        VoucherResponse voucherResponse = voucherService.getVoucher(name);
        voucherService.deleteVoucher(name);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).data(voucherResponse).build();
    }
}
