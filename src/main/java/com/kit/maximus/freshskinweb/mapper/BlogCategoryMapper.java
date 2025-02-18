package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.blogCategory.BlogCategoryCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blogCategory.BlogCategoryUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {
    BlogCategory  toBlogCategory(BlogCategoryCreationRequest request);

    BlogCategoryResponse toBlogCategoryResponse(BlogCategory request);

    @Mapping(target = "blog", ignore = true)
    void updateBlogCategory(@MappingTarget BlogCategory blogCategory, BlogCategoryUpdateRequest productRequestDTO);
}
