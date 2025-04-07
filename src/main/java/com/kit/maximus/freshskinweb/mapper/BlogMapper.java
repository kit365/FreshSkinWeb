package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import org.mapstruct.*;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "user", ignore = true)
    BlogEntity toBlogEntity(BlogCreationRequest request);

    @Mapping(target = "blogCategory", source = "blogCategory")
    BlogResponse toBlogResponse(BlogEntity request);

    List<BlogResponse> toBlogsResponseDTO(List<BlogEntity> request);

    @Mapping(target = "blogCategory", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", source = "status")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBlogEntity(@MappingTarget BlogEntity entity, BlogUpdateRequest request);
}
