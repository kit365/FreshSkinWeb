package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products/category")
public class ProductCategoryController {

    ProductCategoryService productCategoryService;

    @PostMapping("create")
    public ResponseAPI<ProductCategoryResponse> create(@RequestBody CreateProductCategoryRequest request) {
        String message = "Create Product successfull";
        ProductCategoryResponse result = productCategoryService.add(request);
        log.info("CREATE CATEGORY_PRODUCT REQUEST)");
        return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }
}
