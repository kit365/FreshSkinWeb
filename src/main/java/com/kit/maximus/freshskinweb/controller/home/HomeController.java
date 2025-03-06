package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.RouterDTO;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        List<String> freshSkinSloganList = Arrays.asList("Nước tẩy trang", "Sữa rữa mặt", "Toner / Nước cân bằng da");
        List<String> topMoisturizingProductsList = Arrays.asList("Tẩy tế bào chết", "Chống nắng da mặt", "Serum / Tinh Chất");
        List<String> beautyTrendsList = Arrays.asList("Làm Sạch Da", "Đặc Trị", "Hỗ trợ trị mụn");


        return Map.of(
                "featuredProductCategory", productCategoryService.getFeaturedProductCategories(),
                "featuredBlogCategory", blogCategoryService.getFeaturedBlogCategories(),
                "Top7ProductFlashSale",productService.findTop7FlashSale(),
                "FreshSkinSlogan",productCategoryService.getFilteredCategories(freshSkinSloganList,6),
                "Top_moisturizing_products",productCategoryService.getFilteredCategories(topMoisturizingProductsList,10),
                "BeautyTrends",productCategoryService.getFilteredCategories(beautyTrendsList,5),
                "Top3ProductFeature",productService.getProductsFeature(),
                "AllBrand",productBrandService.getAll()
    );
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

        Map<String, Object> data = productService.getProductByCategoryOrBrandSlug(size, page, sortValue,sortDirection, slug, brand, category, skinType, minPrice, maxPrice);
        return ResponseAPI.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }
}
