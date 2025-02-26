package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home/product/category")
public class ProductCategoryHomeController {

//    @GetMapping
//    public ResponseAPI<?> getFeaturedCategories(){
//
//    }
}
