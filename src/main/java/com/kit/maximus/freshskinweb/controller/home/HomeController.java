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
                new RouterDTO("HomePage", "/home"),
                new RouterDTO("Danh sách danh mục bài viết", "/home/blogs/category")
        );
    }

    @GetMapping
    public Map<String, Object> getHomeData() {
        return Map.of(
                "featured/Product-Category", productCategoryService.getFeaturedProductCategories(),
                "featured/Blog-Category", blogCategoryService.getFeaturedBlogCategories()
//                "blog-category", blogCategoryService);
    );
    }
}
