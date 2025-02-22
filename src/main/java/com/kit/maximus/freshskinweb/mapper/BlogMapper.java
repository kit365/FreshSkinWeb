package com.kit.maximus.freshskinweb.mapper;

import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import org.mapstruct.*;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    @Mapping(target = "blogCategory", ignore = true)
    BlogEntity toBlogEntity(BlogCreationRequest request);

    @Mapping(target = "blogCategory", source = "blogCategory")
    BlogResponse toBlogResponse(BlogEntity request);

    @Mapping(target = "blogCategory", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBlogEntity(@MappingTarget BlogEntity entity, BlogUpdateRequest request);
}
