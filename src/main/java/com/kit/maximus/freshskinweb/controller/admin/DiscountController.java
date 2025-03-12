package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.discount.DiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.DiscountService;
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
    public ResponseAPI<Boolean> create(@RequestBody DiscountRequest request){
        String message = "Create discount success";
       var result =  discountService.addDiscount(request);
       if(!result){
           message = "Create discount failed";
       }
    return ResponseAPI.<Boolean>builder()
            .code(HttpStatus.OK.value())
            .message(message)
            .build();
    }

    @GetMapping("{id}")
    public ResponseAPI<DiscountResponse> getDiscount(@PathVariable String id){
        var result = discountService.getDiscount(id);
        String message = "Get discount success";
        if(result == null){
            message = "Create discount failed";
        }
        return ResponseAPI.<DiscountResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllDiscounts(
            @RequestParam(required = false) String promoCode,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) Boolean isGlobal,
            @RequestParam(defaultValue = "false") Boolean sortByUsed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET ALL DISCOUNTS");
        log.info("Received promoCode: {}", promoCode);
        log.info("Received discountType: {}", discountType);
        log.info("Received isGlobal: {}", isGlobal);
        log.info("Received sortByUsed: {}", sortByUsed);

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> result = discountService.getAllDiscounts(
                promoCode, discountType, isGlobal, sortByUsed, pageable
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
                .message("Update discount success")
                .data(result)
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseAPI<Boolean> deleteDiscount(@PathVariable String id){
        var result = discountService.deleteDiscount(id);
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Delete discount success")
                .data(result)
                .build();
    }

    @DeleteMapping
    public ResponseAPI<Boolean> deleteDiscounts(){
        var result = discountService.deleteDiscount();
        return ResponseAPI.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Delete discount success")
                .data(result)
                .build();
    }
}
