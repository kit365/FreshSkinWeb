package com.kit.maximus.freshskinweb.service.home;

import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductBrandResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.service.product.ProductBrandService;
import com.kit.maximus.freshskinweb.service.product.ProductCategoryService;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import com.kit.maximus.freshskinweb.service.blog.BlogCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class HomeService {
    ProductCategoryService productCategoryService;

    BlogCategoryService blogCategoryService;

    ProductService productService;

    ProductBrandService productBrandService;

    static final List<String> freshSkinSloganList = Arrays.asList("Nước tẩy trang", "Sữa rửa mặt", "Toner / Nước cân bằng da");
    static final List<String> topMoisturizingProductsList = Arrays.asList("Tẩy tế bào chết", "Chống nắng da mặt", "Serum / Tinh Chất");
    static final List<String> beautyTrendsList = Arrays.asList("Dầu Gội", "Dầu Xả", "Xịt Dưỡng Tóc");

    @Async()
    public CompletableFuture<List<ProductCategoryResponse>> getFeaturedProductCategoriesAsync() {
        List<ProductCategoryResponse> categories = productCategoryService.getFeaturedProductCategories();
        return CompletableFuture.completedFuture(categories);
    }

    @Async()
    public CompletableFuture<List<ProductCategoryResponse>> getFreshSkinSloganList() {
        List<ProductCategoryResponse> categoryResponses = productCategoryService.getFilteredCategories(freshSkinSloganList, 6);
        return CompletableFuture.completedFuture(categoryResponses);
    }

    @Async()
    public CompletableFuture<List<ProductCategoryResponse>> getTopMoisturizingProductsList() {
        List<ProductCategoryResponse> categoryResponses = productCategoryService.getFilteredCategories(topMoisturizingProductsList, 10);
        return CompletableFuture.completedFuture(categoryResponses);
    }

    @Async()
    public CompletableFuture<List<ProductCategoryResponse>> getBeautyTrendsList() {
        List<ProductCategoryResponse> categoryResponses = productCategoryService.getFilteredCategories(beautyTrendsList, 7);
        return CompletableFuture.completedFuture(categoryResponses);
    }

    @Async()
    public CompletableFuture<List<ProductCategoryResponse>> getListCategory() {
        List<ProductCategoryResponse> categoryResponses = productCategoryService.showALL();
        return CompletableFuture.completedFuture(categoryResponses);
    }

    @Async()
    public CompletableFuture<List<ProductBrandResponse>> getListBrands() {
        List<ProductBrandResponse> brandResponses = productBrandService.getTop10();
        return CompletableFuture.completedFuture(brandResponses);
    }

    @Async()
    public CompletableFuture<List<BlogCategoryResponse>> getListBlogsCategoryFeature() {
        List<BlogCategoryResponse> blogCategoryResponses = blogCategoryService.getFeaturedBlogCategories();
        return CompletableFuture.completedFuture(blogCategoryResponses);
    }

    @Async
    public CompletableFuture<List<ProductResponseDTO>> getTop3ProductsFeature() {
        List<ProductResponseDTO> productResponseDTOS = productService.getProductsFeature();
        return CompletableFuture.completedFuture(productResponseDTOS);
    }


    @Async
    public CompletableFuture<List<ProductResponseDTO>> getTop7ProductFlashSale() {
        List<ProductResponseDTO> productResponseDTOS = productService.findTop7FlashSale();
        return CompletableFuture.completedFuture(productResponseDTOS);
    }

    @Async
    public CompletableFuture<List<ProductResponseDTO>> getTop10SellingProducts() {
        List<ProductResponseDTO> productResponseDTOS = productService.top10SellingProducts();
        return CompletableFuture.completedFuture(productResponseDTOS);
    }




}
