package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.blog_category.CreateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {

    @Mapping(target = "blog", ignore = true)
    BlogCategoryEntity toBlogCategory(CreateBlogCategoryRequest request);

    @Mapping(target = "blog", source = "blog")
    BlogCategoryResponse toBlogCategoryResponse(BlogCategoryEntity request);

    List<BlogCategoryResponse> toBlogCateroiesResponseDTO(List<BlogCategoryEntity> request);


    @Mapping(target = "blog", ignore = true)
    void updateBlogCategory(@MappingTarget BlogCategoryEntity blogCategoryEntity, UpdateBlogCategoryRequest productRequestDTO);
}
