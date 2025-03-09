package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.discount.CreationDiscountRequest;
import com.kit.maximus.freshskinweb.dto.request.discount.UpdationtionDiscountRequest;
import com.kit.maximus.freshskinweb.dto.response.DiscountResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/discount")
public class DiscountController {

    DiscountService discountService;

    @PostMapping("create")
    public ResponseAPI<Boolean> create(@RequestBody CreationDiscountRequest request){
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
    public ResponseAPI<DiscountResponse> getDiscount(@PathVariable Long id){
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

    @GetMapping
    public ResponseAPI<List<DiscountResponse>> getAllDiscounts(){
        var result = discountService.getAllDiscounts();
        String message = "Get discount success";
        if(result == null){
            message = "Create discount failed";
        }
        return ResponseAPI.<List<DiscountResponse>>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

    @PutMapping("edit/{id}")
    public ResponseAPI<DiscountResponse> updateDiscount(@PathVariable Long id, @RequestBody UpdationtionDiscountRequest request){
        var result = discountService.updateDiscount(id, request);
        return ResponseAPI.<DiscountResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update discount success")
                .data(result)
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseAPI<Boolean> deleteDiscount(@PathVariable Long id){
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
