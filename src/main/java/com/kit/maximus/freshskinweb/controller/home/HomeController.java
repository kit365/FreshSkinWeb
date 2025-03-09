package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.RouterDTO;
import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.service.BlogCategoryService;
import com.kit.maximus.freshskinweb.service.ProductBrandService;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("home")
public class HomeController {

    ProductCategoryService productCategoryService;

    BlogCategoryService blogCategoryService;

    ProductService productService;

    ProductBrandService productBrandService;
    private final RestClient.Builder builder;


    @GetMapping("/routes")
    public List<RouterDTO> getRoutes() {
        return List.of(
                new RouterDTO("Trang chủ", "/home"),
                new RouterDTO("Thương hiệu", "/brands"),
                new RouterDTO("Dưỡng da", "/skincare"),
                new RouterDTO("Khuyến mãi HOT", "/promotions"),
                new RouterDTO("Sản phẩm mới", "/new-products"),
                new RouterDTO("Top bán chạy", "/best-sellers"),
                new RouterDTO("So sánh sản phẩm", "/compare"),
                new RouterDTO("Loại Da Của Bạn", "/skin-type"),
                new RouterDTO("Tạp Chí Làm Đẹp", "/home/blogs")
        );
    }

    //    @Cacheable("homeData")
    @GetMapping
    public Map<String, Object> getHomeData() {
        int threadCount = 9;

        List<String> freshSkinSloganList = Arrays.asList("Nước tẩy trang", "Sữa rữa mặt", "Toner / Nước cân bằng da");
        List<String> topMoisturizingProductsList = Arrays.asList("Tẩy tế bào chết", "Chống nắng da mặt", "Serum / Tinh Chất");
        List<String> beautyTrendsList = Arrays.asList("Dầu Gội", "Dầu Xả", "Xịt Dưỡng Tóc");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // Tạo ThreadPool để quản lý luồng

        CompletableFuture<List<ProductCategoryResponse>> freshSkinFuture =
                CompletableFuture.supplyAsync(() -> productCategoryService.getFilteredCategories(freshSkinSloganList, 6), executor);

        CompletableFuture<List<ProductCategoryResponse>> topMoisturizingFuture =
                CompletableFuture.supplyAsync(() -> productCategoryService.getFilteredCategories(topMoisturizingProductsList, 10), executor);

        CompletableFuture<List<ProductCategoryResponse>> beautyTrendsFuture =
                CompletableFuture.supplyAsync(() -> productCategoryService.getFilteredCategories(beautyTrendsList, 5), executor);

        CompletableFuture<List<ProductBrandResponse>> listBrandsFuture =
                CompletableFuture.supplyAsync(productBrandService::getAll, executor);

        CompletableFuture<List<ProductCategoryResponse>> listProductCategoryFutute =
                CompletableFuture.supplyAsync(productCategoryService::showALL, executor);

        CompletableFuture<List<BlogCategoryResponse>> listBlogCategoryFeatureFuture =
                CompletableFuture.supplyAsync(blogCategoryService::getFeaturedBlogCategories, executor);

        CompletableFuture<List<ProductCategoryResponse>> featuredProductCategoryFutute = CompletableFuture.supplyAsync(productCategoryService::getFeaturedProductCategories, executor);

        CompletableFuture<List<ProductResponseDTO>> Top7ProductFlashSaleFutute = CompletableFuture.supplyAsync(productService::findTop7FlashSale, executor);

        CompletableFuture<List<ProductResponseDTO>> Top3ProductFeatureFutute = CompletableFuture.supplyAsync(productService::getProductsFeature, executor);
        // Đợi tất cả hoàn thành
        CompletableFuture.allOf(freshSkinFuture, topMoisturizingFuture, beautyTrendsFuture, listBrandsFuture, listProductCategoryFutute, listBlogCategoryFeatureFuture, featuredProductCategoryFutute, Top7ProductFlashSaleFutute, Top3ProductFeatureFutute).join();

        try {
            return Map.of(
                    "featuredProductCategory", featuredProductCategoryFutute.get(),
                    "featuredBlogCategory", listBlogCategoryFeatureFuture.get(),
                    "Top7ProductFlashSale", Top7ProductFlashSaleFutute.get(),
                    "FreshSkinSlogan", freshSkinFuture.get(),
                    "Top_moisturizing_products", topMoisturizingFuture.get(),
                    "BeautyTrends", beautyTrendsFuture.get(),
                    "Top3ProductFeature", Top3ProductFeatureFutute.get(),
                    "AllBrand", listBrandsFuture.get(),
                    "AllCategory", listProductCategoryFutute.get()
            );
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh mục", e);
        } finally {
            executor.shutdown(); // Đóng ThreadPool sau khi hoàn thành
        }

    }

    @GetMapping("/{slug}")
    public ResponseAPI<Map<String, Object>> getProductByCategorySlug(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "position") String sortValue,
            @PathVariable("slug") String slug,
            @RequestParam(value = "brand", required = false) List<String> brand,
            @RequestParam(value = "category", required = false) List<String> category,
            @RequestParam(value = "skinType", required = false) List<String> skinType,
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = "0") double maxPrice) {

        Map<String, Object> data = productService.getProductByCategoryOrBrandSlug(size, page, sortValue, sortDirection, slug, brand, category, skinType, minPrice, maxPrice);
        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    @GetMapping("search")
    public ResponseAPI<Map<String, Object>> getProductBySearch(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "position") String sortValue,
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "brand", required = false) List<String> brand,
            @RequestParam(value = "category", required = false) List<String> category,
            @RequestParam(value = "skinType", required = false) List<String> skinType,
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = "0") double maxPrice) {

        Map<String, Object> data = productService.getProductsByKeyword(size, page, sortValue, sortDirection, keyword, brand, category, skinType, minPrice, maxPrice);
        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    @PostMapping(value = "suggest", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseAPI<List<ProductResponseDTO>> getProductBySuggest(@RequestBody String keyword) {

        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        var result = productService.suggestProduct(keyword);

        return ResponseAPI.<List<ProductResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

}
