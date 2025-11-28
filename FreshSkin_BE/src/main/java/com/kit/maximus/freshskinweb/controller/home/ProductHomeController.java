package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.request.productcomparison.ProductComparisonDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.productcomparison.ProductComparisonResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductComparisonEntity;
import com.kit.maximus.freshskinweb.service.product.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.product.ProductComparisonService;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/products")
public class ProductHomeController {

    ProductService productService;

    ProductComparisonService productComparisonService;

    @GetMapping("{slug}")
    public ResponseAPI<List<Map<String, Object>>> getProductDetail(@PathVariable("slug") String slug) {
        String message_succed = "Thành công!!!";
        String message_failed = "SẢN PHẢM KHÔNG TÌM THẤY";
        try {
            var result = productService.getProductBySlug(slug);
            return ResponseAPI.<List<Map<String, Object>>> builder().code(HttpStatus.OK.value()).data(result).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<List<Map<String, Object>> >builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
        }
    }

    @PostMapping("comparison/save")
    public ResponseAPI<String> save(@RequestBody ProductComparisonDTO request) {
        String message_succed = "Sản phẩm đã được thêm vào danh sách so sánh";
        String message_failed = "Lưu thất bại";
        try {
            productComparisonService.save(request);
            return ResponseAPI.<String> builder().code(HttpStatus.OK.value()).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<String> builder().code(HttpStatus.BAD_REQUEST.value()).message(message_failed).build();
        }
    }

    @PatchMapping("getComparison")
    public ResponseAPI<ProductComparisonResponseDTO> getProductCompare(@RequestBody ProductComparisonDTO request) {
        String message_succed = "Lấy danh sách so sánh thành công";
        String message_failed = "Không tìm thấy danh sách so sánh";
        try {
            ProductComparisonResponseDTO result = productComparisonService.findByID(request.getId(), request.getUserID());
            return ResponseAPI.<ProductComparisonResponseDTO>builder()
                    .code(HttpStatus.OK.value())
                    .data(result)
                    .message(message_succed)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<ProductComparisonResponseDTO>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }


    @DeleteMapping("comparison/delete")
    public ResponseAPI<String> delete(@RequestBody ProductComparisonDTO request) {
        String message_succed = "Xóa sản phẩm trong danh sách thành công";
        String message_failed = "Xóa sản phẩm trong danh sách thất bại";
        try {
            productComparisonService.delete(request);
            return ResponseAPI.<String> builder().code(HttpStatus.OK.value()).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<String> builder().code(HttpStatus.BAD_REQUEST.value()).message(message_failed).build();
        }
    }

}
