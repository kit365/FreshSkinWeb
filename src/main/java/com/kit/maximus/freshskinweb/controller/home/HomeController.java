package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.*;
import com.kit.maximus.freshskinweb.service.blog.BlogCategoryService;
import com.kit.maximus.freshskinweb.service.product.ProductBrandService;
import com.kit.maximus.freshskinweb.service.product.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import com.kit.maximus.freshskinweb.service.home.HomeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("home")
public class HomeController {

    ProductCategoryService productCategoryService;

    BlogCategoryService blogCategoryService;

    ProductService productService;

    HomeService homeService;

    ProductBrandService productBrandService;

    //    @Cacheable("homeData")
//    @GetMapping
//    public Map<String, Object> getHomeData() {
//        int threadCount = 10;
//
//        List<String> freshSkinSloganList = Arrays.asList("Nước tẩy trang", "Sữa rữa mặt", "Toner / Nước cân bằng da");
//        List<String> topMoisturizingProductsList = Arrays.asList("Tẩy tế bào chết", "Chống nắng da mặt", "Serum / Tinh Chất");
//        List<String> beautyTrendsList = Arrays.asList("Dầu Gội", "Dầu Xả", "Xịt Dưỡng Tóc");
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // Tạo ThreadPool để quản lý luồng
//
//        CompletableFuture<List<ProductCategoryResponse>> freshSkinFuture =
//                CompletableFuture.supplyAsync(() -> productCategoryService.getFilteredCategories(freshSkinSloganList, 6), executor);
//
//        CompletableFuture<List<ProductCategoryResponse>> topMoisturizingFuture =
//                CompletableFuture.supplyAsync(() -> productCategoryService.getFilteredCategories(topMoisturizingProductsList, 10), executor);
//
//        CompletableFuture<List<ProductCategoryResponse>> beautyTrendsFuture =
//                CompletableFuture.supplyAsync(() -> productCategoryService.getFilteredCategories(beautyTrendsList, 5), executor);
//
//        CompletableFuture<List<ProductBrandResponse>> listBrandsFuture =
//                CompletableFuture.supplyAsync(productBrandService::getTop10, executor);
//
//        CompletableFuture<List<ProductCategoryResponse>> listProductCategoryFutute =
//                CompletableFuture.supplyAsync(productCategoryService::showALL, executor);
//
//        CompletableFuture<List<BlogCategoryResponse>> listBlogCategoryFeatureFuture =
//                CompletableFuture.supplyAsync(blogCategoryService::getFeaturedBlogCategories, executor);
//
//        CompletableFuture<List<ProductCategoryResponse>> featuredProductCategoryFutute = CompletableFuture.supplyAsync(productCategoryService::getFeaturedProductCategories, executor);
//
//        CompletableFuture<List<ProductResponseDTO>> Top7ProductFlashSaleFutute = CompletableFuture.supplyAsync(productService::findTop7FlashSale, executor);
//
//        CompletableFuture<List<ProductResponseDTO>> Top3ProductFeatureFutute = CompletableFuture.supplyAsync(productService::getProductsFeature, executor);
//
//        CompletableFuture<List<ProductResponseDTO>> Top10ProductSellerFeatureFutute = CompletableFuture.supplyAsync(productService::top10SellingProducts, executor);
//        // Đợi tất cả hoàn thành
//        CompletableFuture.allOf(freshSkinFuture, topMoisturizingFuture, beautyTrendsFuture, listBrandsFuture, listProductCategoryFutute, listBlogCategoryFeatureFuture, featuredProductCategoryFutute, Top7ProductFlashSaleFutute, Top3ProductFeatureFutute, Top10ProductSellerFeatureFutute).join();
//
//        try {
//            return Map.of(
//                    "featuredProductCategory", featuredProductCategoryFutute.get(),
//                    "featuredBlogCategory", listBlogCategoryFeatureFuture.get(),
//                    "Top7ProductFlashSale", Top7ProductFlashSaleFutute.get(),
//                    "FreshSkinSlogan", freshSkinFuture.get(),
//                    "Top_moisturizing_products", topMoisturizingFuture.get(),
//                    "BeautyTrends", beautyTrendsFuture.get(),
//                    "Top3ProductFeature", Top3ProductFeatureFutute.get(),
//                    "AllBrand", listBrandsFuture.get(),
//                    "AllCategory", listProductCategoryFutute.get(),
//                    "Top10ProductSeller", Top10ProductSellerFeatureFutute.get()
//            );
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi lấy danh mục", e);
//        } finally {
//            executor.shutdown(); // Đóng ThreadPool sau khi hoàn thành
//        }
//    }


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
            @RequestParam(value = "keyword") String keyword) {

        Map<String, Object> data = productService.getProductsByKeyword(keyword, size, page);
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

    @GetMapping
    public Map<String, Object> getHomeData() {
        CompletableFuture<List<ProductCategoryResponse>> featuredProductCategoryFutute = homeService.getFeaturedProductCategoriesAsync();
        CompletableFuture<List<ProductBrandResponse>> listBrandsFuture = homeService.getListBrands();
        CompletableFuture<List<ProductCategoryResponse>> listProductCategoryFutute = homeService.getListCategory();
        CompletableFuture<List<ProductCategoryResponse>> freshSkinFuture = homeService.getFreshSkinSloganList();
        CompletableFuture<List<ProductCategoryResponse>> topMoisturizingFuture = homeService.getTopMoisturizingProductsList();
        CompletableFuture<List<ProductCategoryResponse>> beautyTrendsFuture = homeService.getBeautyTrendsList();
        CompletableFuture<List<BlogCategoryResponse>> listBlogCategoryFeatureFuture = homeService.getListBlogsCategoryFeature();
        CompletableFuture<List<ProductResponseDTO>> Top3ProductFeatureFuture = homeService.getTop3ProductsFeature();

        CompletableFuture<List<ProductResponseDTO>> Top7ProductFlashSaleFuture = homeService.getTop7ProductFlashSale();
        CompletableFuture<List<ProductResponseDTO>> Top10ProductSellerFeatureFuture = homeService.getTop10SellingProducts();


        try {
            return Map.ofEntries(
                    Map.entry("featuredProductCategory", featuredProductCategoryFutute.get()),
                    Map.entry("AllBrand", listBrandsFuture.get()),
                    Map.entry("AllCategory", listProductCategoryFutute.get()),
                    Map.entry("FreshSkinSlogan", freshSkinFuture.get()),
                    Map.entry("Top_moisturizing_products", topMoisturizingFuture.get()),
                    Map.entry("BeautyTrends", beautyTrendsFuture.get()),
                    Map.entry("featuredBlogCategory", listBlogCategoryFeatureFuture.get()),
                    Map.entry("Top3ProductFeature", Top3ProductFeatureFuture.get()),
                    Map.entry("Top10ProductSeller", Top10ProductSellerFeatureFuture.get()),
                    Map.entry("Top7ProductFlashSale", Top7ProductFlashSaleFuture.get())
            );


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
