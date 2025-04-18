package com.kit.maximus.freshskinweb.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.OrderResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.order.OrderService;
import com.kit.maximus.freshskinweb.service.product.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.product.ProductService;
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

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products/category")
public class ProductCategoryAdminController {

    ProductCategoryService productCategoryService;

    OrderService orderService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductCategoryResponse> createProductCategory(@RequestPart String request,
                                                                      @RequestPart(value = "thumbnail", required = false) List<MultipartFile> file) {
        String message_succed = "Tạo danh mục sản phẩm thành công";
        String message_failed = "Tạo danh mục sản phẩm thất bại";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CreateProductCategoryRequest createProductCategoryequest = objectMapper.readValue(request, CreateProductCategoryRequest.class);
            createProductCategoryequest.setImage(file);
            var result = productCategoryService.add(createProductCategoryequest);
            log.info("CREATE BRAND_PRODUCT REQUEST)");
            return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.BAD_REQUEST.value()).message(message_failed).build();
        }
    }


    @PostMapping("indexed")
    public ResponseAPI<String> indexProductCategory() {
        boolean result = productCategoryService.indexProductCategory();
        String message = String.valueOf(result);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }


    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllProductCategory(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "8") int size,
                                                                  @RequestParam(defaultValue = "position") String sortKey,
                                                                  @RequestParam(defaultValue = "desc") String sortValue,
                                                                  @RequestParam(defaultValue = "ALL") String status,
                                                                  @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List ProductCategory";
        log.info("GET ALL PRODUCTS CATEGORY");
        Map<String, Object> result = productCategoryService.getAll(page, size, sortKey, sortValue, status, keyword);

        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<ProductCategoryResponse>> getListProductCategory() {
        var result = productCategoryService.getAll();
        return ResponseAPI.<List<ProductCategoryResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("shows")
    public ResponseAPI<List<ProductCategoryResponse>> getAllListProductCategory() {
        var result = productCategoryService.getAlls();
        return ResponseAPI.<List<ProductCategoryResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }


    @PatchMapping(value = "edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseAPI<ProductCategoryResponse> editBlog(
            @PathVariable("id") Long id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newImg", required = false) List<MultipartFile> newImg) {

        log.info("requestJson:{}", requestJson);
        log.info("images:{}", newImg);
        String message_succed = "Cập nhập danh mục sản phẩm thành công";
        String message_failed = "Cập nhập danh mục sản phẩm thất bại";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateProductCategoryRequest updateProductCategoryRequest = objectMapper.readValue(requestJson, UpdateProductCategoryRequest.class);
            if (newImg != null) {
                updateProductCategoryRequest.setNewImg(newImg);
            }

            ProductCategoryResponse result = productCategoryService.update(id, updateProductCategoryRequest);

            log.info("UPDATE PRODUCT CATEGORY REQUEST SUCCESS");
            return ResponseAPI.<ProductCategoryResponse>builder()
                    .code(HttpStatus.OK.value())
                    .data(result)
                    .message(message_succed)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("UPDATE PRODUCT CATEGORY ERROR: " + e.getMessage());
            return ResponseAPI.<ProductCategoryResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message_failed)
                    .build();
        }
    }

    @PatchMapping("update/{id}")
    public ResponseAPI<String> updateProductCategory(@PathVariable("id") int id,
                                          @RequestBody Map<String, Object> request) {

        String statusEdit = (String) request.get("statusEdit");
        String status = (String) request.get("status");
        int position = request.containsKey("position") ? (int) request.get("position") : 0;

        String result = productCategoryService.update(id, status, position, statusEdit);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("change-multi")
    public ResponseAPI<String> updateProductCategory(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");
        String status = productRequestDTO.get("status").toString();

        var result = productCategoryService.update(ids, status);

        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteProductCategory(@PathVariable("id") Long id) {
        String message_succed = "Delete Product_Category successfull";
        String message_failed = "Delete Product_Category failed";
        boolean result = productCategoryService.delete(id);
        if (result) {
            log.info("Product_Category deleted successfully!");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("deleteT/{id}")
    public ResponseAPI<String> deleteTProductCategory(@PathVariable("id") Long id) {
        String message_succed = "Delete Product_Category successfull";
        String message_failed = "Delete Product_Category failed";
        boolean result = productCategoryService.deleteTemporarily(id);
        if (result) {
            log.info("Product_Category deleted successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }


    @PatchMapping("restore/{id}")
    public ResponseAPI<String> restoreProductCategory(@PathVariable("id") Long id) {
        String message_succed = "restore Product_Category successfull";
        String message_failed = "restore Product_Category failed";
        boolean result = productCategoryService.restore(id);
        if (result) {
            log.info("Product_Category restore successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category restore failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete")
    public ResponseAPI<String> deleteProductCategory(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) productRequestDTO.get("id");

        String message_succed = "delete Category successfull";
        String message_failed = "delete Category failed";
        var result = productCategoryService.delete(ids);
        if (result) {
            log.info("CategoryProduct delete successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("CategoryProduct delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @GetMapping("{id}")
    public ResponseAPI< ProductCategoryResponse> getProductCategory(@PathVariable("id") Long id) {
       ProductCategoryResponse result  =  productCategoryService.showDetail(id);
        return ResponseAPI.<ProductCategoryResponse> builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("test")
    public Map<String, Object> test() {
            return productCategoryService.getRevenueByCategories();
    }




}
