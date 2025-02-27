package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.RouterDTO;
import com.kit.maximus.freshskinweb.service.BlogCategoryService;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        return Map.of(
                "featuredProductCategory", productCategoryService.getFeaturedProductCategories(),
                "featuredBlogCategory", blogCategoryService.getFeaturedBlogCategories()
//                "blog-category", blogCategoryService);
    );
    }
}
