package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/products")
public class ProductHomeController {

    ProductService productService;

    ProductCategoryService productCategoryService;

    @GetMapping("{slug}")
    public ResponseAPI<List<Map<String, Object>>> getProductDetail(@PathVariable("slug") String slug) {
        String message_succed = "Thành công!!!";
        String message_failed = "Thất bại!!!";
        try {
            var result = productService.getProductBySlug(slug);
            return ResponseAPI.<List<Map<String, Object>>> builder().code(HttpStatus.OK.value()).data(result).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<List<Map<String, Object>> >builder().code(HttpStatus.OK.value()).message(message_failed).build();
        }
    }

    @GetMapping("show/bodycare/{slug}")
    public ResponseAPI<Map<String, Object>> getProductDetailBodyCare(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "desc") String sortValue,
            @PathVariable("slug") String slug) {

        Map<String, Object> data = productService.getBodyCare(size, page, sortValue, slug);
        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }



}
