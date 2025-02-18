package com.kit.maximus.freshskinweb.controller.productbrand;

import com.kit.maximus.freshskinweb.dto.request.product_brand.CreateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.request.product_brand.UpdateProductBrandRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductBrandResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.service.ProductBrandService;
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
@RequestMapping("admin/products/brand/trash")
public class ProductBrandControllerTrash {
    ProductBrandService productBrandService;

    @PostMapping("create")
    public ResponseAPI<ProductBrandResponse> create(@RequestBody CreateProductBrandRequest request) {
        String message = "Create product_brand successfull";
        ProductBrandResponse result = productBrandService.add(request);
        log.info("CREATE BRAND_PRODUCT REQUEST)");
        return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping()
    public ResponseAPI<Map<String, Object>> getAllProduct(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "4") int size,
                                                          @RequestParam(defaultValue = "position") String sortKey,
                                                          @RequestParam(defaultValue = "desc") String sortValue,
                                                          @RequestParam(defaultValue = "ALL") String status,
                                                          @RequestParam(name = "keyword", required = false) String keyword) {
        String message = "Tim thay List ProductBrand";
        log.info("GET ALL PRODUCTS BRAND");
        Map<String, Object> result = productBrandService.getTrash(page, size, sortKey, sortValue, status, keyword);

        return ResponseAPI.<Map<String, Object>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @GetMapping("show")
    public ResponseAPI<List<ProductBrandResponse>> getList() {
        var result = productBrandService.getAll();
        return ResponseAPI.<List<ProductBrandResponse>>builder().code(HttpStatus.OK.value()).data(result).build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<ProductBrandResponse> updateProduct(@PathVariable("id") Long id, @RequestBody UpdateProductBrandRequest request) {
        ProductBrandResponse result = productBrandService.update(id, request);
        String message_succed = "Update product_brand successfull";
        String message_failed = "Update product_brand failed";
        if (result != null) {
            log.info("Product_brand updated successfully");
            return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.OK.value()).message(message_succed).data(result).build();
        }
        log.info("Product_brand update failed");
        return ResponseAPI.<ProductBrandResponse>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @PatchMapping("updateStatus")
    public ResponseAPI<String> updateProduct(@RequestBody Map<String, Object> productRequestDTO) {

        if (!productRequestDTO.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        String message_succed = "Update Status Product_brand successfull";
        String message_failed = "Update Status Product_brand failed";

        List<Long> ids = (List<Long>) productRequestDTO.get("id");
        String status = productRequestDTO.get("status").toString();

        boolean result = productBrandService.update(ids, status);
        if (result) {
            log.info("Product_brand update successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_brand update failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseAPI<String> deleteProduct(@PathVariable("id") Long id) {
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
    public ResponseAPI<String> deleteProductT(@PathVariable("id") Long id) {
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


    @PatchMapping("deleteT")
    public ResponseAPI<String> deleteProductT(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "Delete Product_Category successfull";
        String message_failed = "Delete Product_Category failed";
        var result = productBrandService.deleteTemporarily(ids);
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
        boolean result = productBrandService.restore(id);
        if (result) {
            log.info("Product_Category restore successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category restore failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }


    @PatchMapping("restore")
    public ResponseAPI<String> restore(@RequestBody Map<String, Object> request) {

        if (!request.containsKey("id")) {
            log.warn("Request does not contain 'id' key");
            throw new AppException(ErrorCode.INVALID_REQUEST_PRODUCTID);
        }

        List<Long> ids = (List<Long>) request.get("id");

        String message_succed = "restore Product_Category successfull";
        String message_failed = "restore Product_Category failed";
        var result = productBrandService.restore(ids);
        if (result) {
            log.info("Product_Category restore successfully");
            return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message_succed).build();
        }
        log.info("Product_Category restore failed");
        return ResponseAPI.<String>builder().code(HttpStatus.NOT_FOUND.value()).message(message_failed).build();
    }

}
