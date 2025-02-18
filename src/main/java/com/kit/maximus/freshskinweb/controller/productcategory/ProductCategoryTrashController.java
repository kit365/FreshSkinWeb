package com.kit.maximus.freshskinweb.controller.productcategory;

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

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products/category/trash")
public class ProductCategoryTrashController {

    ProductCategoryService productCategoryService;

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllProduct(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "4") int size,
                                                          @RequestParam(defaultValue = "position") String sortKey,
                                                          @RequestParam(defaultValue = "desc") String sortValue,
                                                          @RequestParam(defaultValue = "ALL") String status,
                                                          @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List ProductCategory";
        log.info("GET ALL PRODUCTS CATEGORY");
        Map<String, Object> result = productCategoryService.getTrash(page, size,sortKey, sortValue,status,keyword);

        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<ProductCategoryResponse>> getList() {
        var result = productCategoryService.getAll();
        return ResponseAPI.<List<ProductCategoryResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<ProductCategoryResponse> updateProduct(@PathVariable("id") Long id, @RequestBody UpdateProductCategoryRequest request) {
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

    @PatchMapping("updateStatus")
    public ResponseAPI<String> updateProduct(@RequestBody Map<String,Object>  productRequestDTO) {

        if(!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        String message_succed = "Update Status Product_Category successfull";
        String message_failed = "Update Status Product_Category failed";

        List<Long> ids =  (List<Long>) productRequestDTO.get("id");
        String status  =  productRequestDTO.get("status").toString();

        boolean result = productCategoryService.update(ids, status);
        if (result) {
            log.info("Product_Category update successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category update failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteProduct(@PathVariable("id") Long id) {
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
    public ResponseAPI<String> deleteProductT(@PathVariable("id") Long id) {
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


    @PatchMapping("deleteT")
    public ResponseAPI<String> deleteProductT(@RequestBody Map<String,Object> request) {

        if(!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "Delete Product_Category successfull";
        String message_failed = "Delete Product_Category failed";
        var result = productCategoryService.deleteTemporarily(ids);
        if (result) {
            log.info("Product_Category deleted successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category delete failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("restore/{id}")
    public ResponseAPI<String> restore(@PathVariable("id") Long id) {
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


    @PatchMapping("restore")
    public ResponseAPI<String> restore(@RequestBody Map<String,Object> request) {

        if(!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "restore Product_Category successfull";
        String message_failed = "restore Product_Category failed";
        var result = productCategoryService.restore(ids);
        if (result) {
            log.info("Product_Category restore successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category restore failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }
}
