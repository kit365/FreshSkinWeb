package com.kit.maximus.freshskinweb.dto.request.blogCategory;

import com.kit.maximus.freshskinweb.entity.BlogEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogCategoryCreationRequest {
    String blogCategoryName;
    String description;
    List<BlogEntity> blog;
}
