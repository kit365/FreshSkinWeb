package com.kit.maximus.freshskinweb.dto.request.blog_category;

import com.kit.maximus.freshskinweb.entity.BlogEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateBlogCategoryRequest implements Serializable {
    String title;
    String description;
    Integer position;
    String status;
    boolean deleted;
    List<BlogEntity> blog;
}
