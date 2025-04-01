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

    @GetMapping("/{id}")
    public ResponseAPI<VoucherResponse> getVoucher(@PathVariable String id) {
        String message = "Lấy voucher thành công";
        VoucherResponse voucherResponse = voucherService.getVoucher(id);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).data(voucherResponse).build();
    }

    @PatchMapping("/update/{id}")
    public ResponseAPI<VoucherResponse> updateVoucher(@PathVariable String id, @RequestBody VoucherRequest voucherRequest) {
        String message = "Cập nhật voucher thành công";
        voucherService.updateVoucher(id, voucherRequest);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping()
    public ResponseAPI<List<VoucherResponse>> getAllVouchers() {
        String message = "Lấy danh sách voucher thành công";
        return ResponseAPI.<List<VoucherResponse>>builder().code(HttpStatus.OK.value()).message(message).data(voucherService.getAllVouchers()).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<VoucherResponse> deleteVoucher(@PathVariable String id) {
        String message = "Xóa voucher thành công";
        VoucherResponse voucherResponse = voucherService.getVoucher(id);
        voucherService.deleteVoucher(id);
        return ResponseAPI.<VoucherResponse>builder().code(HttpStatus.OK.value()).message(message).data(voucherResponse).build();
    }

    @GetMapping("/get")
    public ResponseAPI<List<VoucherResponse>> getFourVoucher() {
        String message = "Xóa voucher thành công";
        List<VoucherResponse> voucher = voucherService.getFourVoucher();
        return ResponseAPI.<List<VoucherResponse>>builder().code(HttpStatus.OK.value()).message(message).data(voucher).build();
    }

}
