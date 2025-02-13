package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.product.CreateProductRequest;
import com.kit.maximus.freshskinweb.dto.request.product.UpdateProductRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
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
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("admin/products")
public class ProductController {

    ProductService productService;

    @PostMapping("create")
    public ResponseAPI<ProductResponseDTO> createProduct(@RequestBody CreateProductRequest productRequestDTO) {
        String message = "Create Product successfull";
        var result = productService.add(productRequestDTO);
        log.info("CREATE PRODUCT REQUEST)");
        return ResponseAPI.<ProductResponseDTO>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllProduct(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "4") int size,
                                                          @RequestParam(defaultValue = "position") String sortKey,
                                                          @RequestParam(defaultValue = "desc") String sortValue,
                                                          @RequestParam(defaultValue = "ALL") String status,
                                                          @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List Product";
        log.info("GET ALL PRODUCTS");
        Map<String, Object> result = productService.getAll(page, size,sortKey, sortValue,status,keyword);

        if (result == null) {
            return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.NOT_FOUND.value()).message("Not Found").build();
        }
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @PatchMapping("update/{id}")
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

    @PatchMapping("updateStatus")
    public ResponseAPI<String> updateProduct(@RequestBody Map<String,Object>  productRequestDTO) {

        if(!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        String message_succed = "Update Status Product successfull";
        String message_failed = "Update Status Product failed";

        List<Long> ids =  (List<Long>) productRequestDTO.get("id");
        String status  =  productRequestDTO.get("status").toString();

        boolean result = productService.update(ids, status);
        if (result) {
            log.info("Product update successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product  update failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("updatePosition/{id}")
    public ResponseAPI<String> updateProducts(@PathVariable("id") Long id, @RequestBody UpdateProductRequest productRequestDTO) {
        String message_succed = "Update position Product successfull";
        String message_failed = "Update position Product failed";

        boolean result = productService.update(id, productRequestDTO.getPosition());
        if (result) {
            log.info("Product update  successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product update failed ");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
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

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<String> deleteProductT(@PathVariable("id") Long id) {
        String message_succed = "Delete Product successfull";
        String message_failed = "Delete Product failed";
        boolean result = productService.deleteTemporarily(id);
        if (result) {
            log.info("Product deleted successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("User delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("deleteT")
    public ResponseAPI<String> deleteProductT(@RequestBody Map<String,Object> productRequestDTO) {

        if(!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");

        String message_succed = "Delete Product successfull";
        String message_failed = "Delete Product failed";
        var result = productService.deleteTemporarily(ids);
        if (result) {
            log.info("Products deleted successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Products delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }




}
