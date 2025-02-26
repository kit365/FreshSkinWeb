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


    @Mapping(target = "image", ignore = true)
    BlogCategoryEntity toBlogCategory(CreateBlogCategoryRequest request);

    @Mapping(target = "blogID", ignore = true)
    @Mapping(target = "blogs", ignore = true)
    BlogCategoryResponse toBlogCategoryResponse(BlogCategoryEntity request);

    @Mapping(target = "blogID", ignore = true)
    @Mapping(target = "blogs", ignore = true)
    List<BlogCategoryResponse> toBlogCateroiesResponseDTO(List<BlogCategoryEntity> request);

    @Mapping(target = "blog", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "image",ignore = true)
    void updateBlogCategory(@MappingTarget BlogCategoryEntity blogCategoryEntity, UpdateBlogCategoryRequest productRequestDTO);
}
