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


    @GetMapping
    public Map<String, Object> getHomeData() {
//        List<String> FreshSkinSlogan = Arrays.asList("Gel Rửa Mặt", "Nước tẩy trang", "Nước Hoa Hồng");
//        List<String> Top_moisturizing_products = Arrays.asList("Tẩy Da Chết", "Dụng Cụ / Phụ Kiện Chăm Sóc Da", "Loại sản phẩm");
//        List<String> beautyTrends = Arrays.asList("Tẩy Da Chết", "Nước tẩy trang", "Loại sản phẩm");


        List<String> FreshSkinSlogan = Arrays.asList(" Nước tẩy trang", "Nước tẩy trang", "Sữa rữa mặt");
        List<String> Top_moisturizing_products = Arrays.asList("Nước tẩy trang", "Sữa rữa mặt", "Nước tẩy trang");
        List<String> beautyTrends = Arrays.asList("Nước tẩy trang", "Sữa rữa mặt", "Nước tẩy trang");

        return Map.of(
                "featuredProductCategory", productCategoryService.getFeaturedProductCategories(),
                "featuredBlogCategory", blogCategoryService.getFeaturedBlogCategories(),
                "Top7ProductFlashSale",productService.findTop7FlashSale(),
                "FreshSkinSlogan",productCategoryService.getCategoryResponses(FreshSkinSlogan,6),
                "Top_moisturizing_products",productCategoryService.getCategoryResponses(Top_moisturizing_products,10),
                "BeautyTrends",productCategoryService.getCategoryResponses(beautyTrends,5),
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
