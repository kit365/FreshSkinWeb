package com.kit.maximus.freshskinweb.controller.trash;

import com.kit.maximus.freshskinweb.dto.request.productcategory.CreateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.productcategory.UpdateProductCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products/category/trash")
public class ProductCategoryTrashController {

    ProductCategoryService productCategoryService;

    @PostMapping("create")
    public ResponseAPI<ProductCategoryResponse> createProductCategory(@RequestBody CreateProductCategoryRequest request) {
        String message = "Create product_category successfull";
        var result = productCategoryService.add(request);
        log.info("CREATE CATEGORY_PRODUCT REQUEST)");
        return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.OK.value()).message(message).build();
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
        Map<String, Object> result = productCategoryService.getTrash(page, size, sortKey, sortValue, status, keyword);

        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<ProductCategoryResponse>> getListProductCategory() {
        var result = productCategoryService.getAll();
        return ResponseAPI.<List<ProductCategoryResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<ProductCategoryResponse> updateProductCategory(@PathVariable("id") Long id, @RequestBody UpdateProductCategoryRequest request) {
        ProductCategoryResponse result = productCategoryService.update(id, request);
        String message_succed = "Update product_category successfull";
        String message_failed = "Update product_category failed";
        if (result != null) {
            log.info("Product_Category updated successfully");
            return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
        }
        log.info("Product_Category update failed");
        return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
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
    public ResponseAPI<ProductCategoryResponse> getProductCategory(@PathVariable("id") Long id) {
        ProductCategoryResponse result = productCategoryService.showDetail(id);
        return ResponseAPI.<ProductCategoryResponse>builder().code(HttpStatus.OK.value()).data(result).build();
    }

}
