package com.kit.maximus.freshskinweb.controller;

import com.kit.maximus.freshskinweb.dto.request.ProductRequestDTO;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("admin/products")
public class ProductController {

    final ProductService productService;

    @PostMapping("create")
    public ResponseAPI<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        log.info("CREATE PRODUCT REQUEST)");
        return new ResponseAPI<>(HttpStatus.OK.value(), "Create Product successfull", productService.add(productRequestDTO));
    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllProduct(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "4") int size
                                                          ) {
        log.info("GET ALL PRODUCTS");
        Map<String, Object> result = productService.getAll(page, size);

        if (result == null) {
            return new ResponseAPI<>(HttpStatus.NOT_FOUND.value(), "Not Found");
        }
        return new ResponseAPI<>(HttpStatus.OK.value(), "Tim thay List Product", result);
    }

    @PatchMapping("update/{id}")
    public ResponseAPI<ProductResponseDTO> updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO result = productService.update(id, productRequestDTO);
        if (result != null) {
            log.info("Product updated successfully");
            return new ResponseAPI<>(HttpStatus.OK.value(), "Product updated successfully", result);
        }
        log.info("Product update failed");
        return new ResponseAPI<>(HttpStatus.NOT_FOUND.value(), "Update product failed");
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteProduct(@PathVariable("id") Long id) {
        boolean result = productService.delete(id);
        if (result) {
            log.info("Product deleted successfully!");
            return new ResponseAPI<>(HttpStatus.OK.value(), "Product deleted successfully");
        }
        log.info("Product delete failed");
        return new ResponseAPI<>(HttpStatus.NOT_FOUND.value(), "Delete product failed");
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<String> deleteProductT(@PathVariable("id") Long id) {
        boolean result = productService.deleteTemporarily(id);
        if (result) {
            log.info("Product deleted successfully");
            return new ResponseAPI<>(HttpStatus.OK.value(), "Product deleted successfully");
        }
        log.info("User delete failed");
        return new ResponseAPI<>(HttpStatus.NOT_FOUND.value(), "Product delete failed");
    }

    @PatchMapping("deleteT")
    public ResponseAPI<String> deleteProductT(@RequestBody List<Long> ids) {
        boolean result = productService.deleteTemporarily(ids);
        if (result) {
            log.info("Products deleted successfully");
            return new ResponseAPI<>(HttpStatus.OK.value(), "Products deleted successfully");
        }
        log.info("Products delete failed");
        return new ResponseAPI<>(HttpStatus.NOT_FOUND.value(), "Products delete failed");
    }


}
