package com.kit.maximus.freshskinweb.controller.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductResponseDTO> createProduct(
            @RequestPart("request") String requestJson,  // Nhận JSON dưới dạng String
            @RequestPart(value = "thumbnail", required = false) List<MultipartFile> images) { // Nhận hình ảnh

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", images);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateProductRequest productRequestDTO = objectMapper.readValue(requestJson, CreateProductRequest.class);
            productRequestDTO.setThumbnail(images);

            var result = productService.add(productRequestDTO);

            log.info("CREATE PRODUCT REQUEST SUCCESS");
            return ResponseAPI.<ProductResponseDTO>builder()
                    .code(HttpStatus.OK.value())
                    .message("Tạo sản phẩm thành công")
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("CREATE PRODUCT ERROR: " + e.getMessage());
            return ResponseAPI.<ProductResponseDTO>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Tạo sản phẩm thất bại")
                    .build();
        }
    }

    @PatchMapping(value = "edit/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductResponseDTO> updateProduct(@PathVariable("id") Long id,
                                                         @RequestPart(value = "request") String requestJson,
                                                         @RequestPart(value = "thumbnail",required = false) List<MultipartFile> images) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", images);
        ObjectMapper objectMapper = new ObjectMapper();
        String message_succed = "Cập nhật sản phẩm thành công";
        String message_failed = "Cập nhật sản phẩm thất bại";
        try {
            UpdateProductRequest request = objectMapper.readValue(requestJson, UpdateProductRequest.class);
            request.setThumbnail(images);
            ProductResponseDTO result = productService.update(id, request);
            log.info("Product updated successfully");
            return ResponseAPI.<ProductResponseDTO>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
        } catch (JsonProcessingException e) {
            log.info("Product update failed");
            log.error(e.getMessage());
            return ResponseAPI.<ProductResponseDTO>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
        }
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
        Map<String, Object> result = productService.getAll(page, size, sortKey, sortValue, status, keyword);
        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }


    @PatchMapping("change-multi")
    public ResponseAPI<String> updataProduct(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");
        String status = productRequestDTO.get("status").toString();

        var result = productService.update(ids, status);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
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


    @DeleteMapping("delete")
    public ResponseAPI<String> deleteProduct(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
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
