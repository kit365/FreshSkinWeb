package com.kit.maximus.freshskinweb.controller.trash;

import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/products/trash")
public class ProductTrashController {

    ProductService productService;

    @GetMapping
    public ResponseAPI<Map<String, Object>> getAllProduct(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "4") int size,
                                                                 @RequestParam(defaultValue = "position") String sortKey,
                                                                 @RequestParam(defaultValue = "desc") String sortValue,
                                                                 @RequestParam(name = "status", required = false) String status,
                                                                 @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List Product";
        log.info("GET ALL PRODUCTS");
        Map<String, Object> result = productService.getTrash(page, size,sortKey, sortValue,status,keyword);
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateProduct(@RequestBody Map<String,Object>  productRequestDTO) {
        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = ((List<?>) productRequestDTO.get("id"))
                .stream()
                .map(id -> ((Number) id).longValue())
                .collect(Collectors.toList());

        String status = productRequestDTO.get("status").toString();

        var result = productService.update(ids, status);


        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }
    @PatchMapping("edit/{id}")
    public ResponseAPI<ProductResponseDTO> updateProduct(@PathVariable("id") Long id, @RequestBody UpdateProductRequest productRequestDTO) {
        ProductResponseDTO result = productService.update(id, productRequestDTO);
        String message_succed = "Update Product successfull";
        String message_failed = "Update Product failed";
        if (result != null) {
            log.info("Product updated successfully");
            return ResponseAPI.<ProductResponseDTO>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
        }
        log.info("Product update failed");
        return ResponseAPI.<ProductResponseDTO>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteProduct(@PathVariable("id") Long id) {
        String message_succed = "Delete Product successfull";
        String message_failed = "Delete Product failed";
        boolean result = productService.delete(id);
        if (result) {
            log.info("Product deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<String> restoreProduct(@PathVariable("id") Long id) {
        String message_succed = "restore Product successfull";
        String message_failed = "restore Product failed";
        boolean result = productService.restore(id);
        if (result) {
            log.info("Product restore successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product restore failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<String> deleteTProduct(@PathVariable("id") Long id) {
        String message_succed = "Delete Product successfull";
        String message_failed = "Delete Product failed";
        boolean result = productService.deleteTemporarily(id);
        if (result) {
            log.info("Product deleted successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteProduct(@RequestBody Map<String,Object> productRequestDTO) {

        if(!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");

        String message_succed = "delete Product successfull";
        String message_failed = "delete Product failed";
        var result = productService.delete(ids);
        if (result) {
            log.info("Products delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Products delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @GetMapping("{id}")
    public ResponseAPI<ProductResponseDTO> getProduct(@PathVariable("id") Long id) {
        ProductResponseDTO result = productService.showDetail(id);
        return ResponseAPI.<ProductResponseDTO>builder().code(HttpStatus.OK.value()).data(result).build();
    }

}
