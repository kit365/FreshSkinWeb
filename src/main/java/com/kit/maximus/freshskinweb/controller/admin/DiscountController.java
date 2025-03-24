package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.discount.DiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.product.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/discount")
public class DiscountController {

    DiscountService discountService;

    @PostMapping("create")
    public ResponseAPI<DiscountResponse> create(@RequestBody DiscountRequest request){
        String message = "Tạo mã giảm giá thành công";
       var result =  discountService.addDiscount(request);
       if(result == null){
           message = "Tạo mã giảm giá thất bại";
       }
    return ResponseAPI.<DiscountResponse>builder()
            .code(HttpStatus.OK.value())
            .message(message)
            .data(result)
            .build();
    }

    @GetMapping("/search/{id}")
    public ResponseAPI<DiscountResponse> getDiscount(@PathVariable String id) {
        var result = discountService.getDiscount(id);
        String message = "Lấy thông tin mã giảm giá thành công";
        if (result == null) {
            message = "Lấy thông tin mã giảm giá thất bại";
        }
        return ResponseAPI.<DiscountResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }


    @GetMapping("/show")
    public ResponseAPI<Map<String, Object>> getAllDiscounts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) Boolean isGlobal,
            @RequestParam(defaultValue = "false") Boolean sortByUsed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET ALL DISCOUNTS");
        log.info("Received name: {}", name);
        log.info("Received discountType: {}", discountType);
        log.info("Received isGlobal: {}", isGlobal);
        log.info("Received sortByUsed: {}", sortByUsed);

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> result = discountService.getAllDiscounts(
                name, discountType, isGlobal, sortByUsed, pageable
        );

        if (result.get("discounts") instanceof List && ((List<?>) result.get("discounts")).isEmpty()) {
            return ResponseAPI.<Map<String, Object>>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Không tìm thấy mã giảm giá phù hợp với tiêu chí đã nhập.")
                    .data(result)
                    .build();
        }

        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .message("Tìm thấy danh sách mã giảm giá.")
                .data(result)
                .build();
    }



    @PutMapping("edit/{id}")
    public ResponseAPI<DiscountResponse> updateDiscount(@PathVariable String id, @RequestBody DiscountRequest request){
        var result = discountService.updateDiscount(id, request);
        return ResponseAPI.<DiscountResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Chỉnh sửa mã giảm giá thành công")
                .data(result)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<Boolean> deleteDiscount(@PathVariable String id){
        var result = discountService.deleteDiscount(id);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa mã giảm giá thành công")
                .data(result)
                .build();
    }

    @DeleteMapping("/delete")
    public ResponseAPI<Boolean> deleteDiscounts(){
        var result = discountService.deleteDiscount();
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa tất cả mã giảm giá thành công")
                .data(result)
                .build();
    }

    @PostMapping("/add/{id}")
    public ResponseAPI<Boolean> addProduct(@PathVariable String id, @RequestBody List<Long> productIds){
        Boolean result = discountService.applyDiscountToProducts(id, productIds);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Áp dụng mã giảm giá thành công")
                .data(result)
                .build();
    }

    @PostMapping("/remove/{id}")
    public ResponseAPI<Boolean> removeProduct(@PathVariable String id, @RequestBody List<Long> productIds){
        var result = discountService.removeDiscountFromProducts(id, productIds);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("xóa mã giảm giá thành công")
                .data(result)
                .build();
    }

}
