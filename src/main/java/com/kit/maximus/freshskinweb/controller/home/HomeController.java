package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.RouterDTO;
import com.kit.maximus.freshskinweb.service.ProductCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


    @PutMapping
    public List<RouterDTO> home() {
        return List.of(
                new RouterDTO("Danh mục bài viết", "/home/product/category")
        );
    }

    @GetMapping
    public Map<String, Object> getHomeData() {
        return Map.of(
                "featuredProduct-Category", productCategoryService.getFeaturedCategories());
    }


}
