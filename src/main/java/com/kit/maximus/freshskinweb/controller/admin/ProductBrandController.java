package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.kit.maximus.freshskinweb.dto.request.product_brand.CreateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.request.product_brand.UpdateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductBrandResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.product.ProductBrandService;
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

//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products/brand")
public class ProductBrandController {

    ProductBrandService productBrandService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductBrandResponse> createProductBrand
            (@RequestPart("request") String request,
             @RequestPart(value = "thumbnail", required = false) List<MultipartFile> file) {

        String message_succed = "Tạo thương hiệu sản phẩm thành công";
        String message_failed = "Tạo thương hiệu sản phẩm thất bại";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CreateProductBrandRequest createProductBrandRequest = objectMapper.readValue(request, CreateProductBrandRequest.class);
            createProductBrandRequest.setImage(file);
            var result = productBrandService.add(createProductBrandRequest);
            log.info("CREATE BRAND_PRODUCT REQUEST)");
            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.BAD_REQUEST.value()).message(message_failed).build();
        }

    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllProductBrand(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "8") int size,
                                                               @RequestParam(defaultValue = "position") String sortKey,
                                                               @RequestParam(defaultValue = "desc") String sortValue,
                                                               @RequestParam(defaultValue = "ALL") String status,
                                                               @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List ProductBrand";
        log.info("GET ALL PRODUCTS BRAND");
        Map<String, Object> result = productBrandService.getAll(page, size, sortKey, sortValue, status, keyword);

        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateProductBrand(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");
        String status = productRequestDTO.get("status").toString();

        var result = productBrandService.update(ids, status);

        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }


    @GetMapping("show")
    public ResponseAPI<List<ProductBrandResponse>> getListProductBrand() {
        var result = productBrandService.getAll();
        return ResponseAPI.<List<ProductBrandResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

//    @PatchMapping(value = "edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseAPI<ProductBrandResponse> updateProduct(@PathVariable("id") Long id,
//                                                           @RequestPart(value = "request") String requestJson,
//                                                           @RequestPart(value = "thumbnail", required = false) List<MultipartFile> images) {
//
//        log.info("requestJson:{}", requestJson);
//        log.info("images:{}", images);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String message_succed = "Cập nhật thương hiệu sản phẩm thành công";
//        String message_failed = "Cập nhật thương hiệu sản phẩm thất bại";
//        try {
//            UpdateProductBrandRequest request = objectMapper.readValue(requestJson, UpdateProductBrandRequest.class);
//            request.setImage(images);
//            ProductBrandResponse result = productBrandService.update(id, request);
//            log.info("ProductBrand updated successfully");
//            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
//        } catch (JsonProcessingException e) {
//            log.info("ProductBrand update failed");
//            log.error(e.getMessage());
//            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
//        }
//    }

    @PatchMapping(value = "edit/{id}")
    public ResponseAPI<ProductBrandResponse> updateProduct(@PathVariable("id") Long id,
                                                           @RequestBody UpdateProductBrandRequest updateProductBrandRequest) {

        String message_succed = "Cập nhật thương hiệu sản phẩm thành công";
        String message_failed = "Cập nhật thương hiệu sản phẩm thất bại";
        try {
            ProductBrandResponse result = productBrandService.update(id, updateProductBrandRequest);
            log.info("ProductBrand updated successfully");
            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
        } catch (Exception e) {
            log.info("ProductBrand update failed");
            log.error(e.getMessage());
            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
        }
    }



    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteProductBrand(@PathVariable("id") Long id) {
        String message_succed = "Delete Product_brand successfull";
        String message_failed = "Delete Product_brand failed";
        boolean result = productBrandService.delete(id);
        if (result) {
            log.info("Product_brand deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_brand delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<String> deleteTProductBrand(@PathVariable("id") Long id) {
        String message_succed = "Delete Product_brand successfull";
        String message_failed = "Delete Product_brand failed";
        boolean result = productBrandService.deleteTemporarily(id);
        if (result) {
            log.info("Product_brand deleted successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_brand delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<String> restoreProductBrand(@PathVariable("id") Long id) {
        String message_succed = "restore Product_Category successfull";
        String message_failed = "restore Product_Category failed";
        boolean result = productBrandService.restore(id);
        if (result) {
            log.info("Product_Category restore successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category restore failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }


    @DeleteMapping("delete")
    public ResponseAPI<String> deleteProductBrand(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");

        String message_succed = "delete Brand successfull";
        String message_failed = "delete Brand failed";
        var result = productBrandService.delete(ids);
        if (result) {
            log.info("BrandProduct delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("BrandProduct delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @GetMapping("{id}")
    public ResponseAPI<ProductBrandResponse> getProductBrand(@PathVariable("id") Long id) {
        ProductBrandResponse result = productBrandService.showDetail(id);
        return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.OK.value()).data(result).build();
    }


}
