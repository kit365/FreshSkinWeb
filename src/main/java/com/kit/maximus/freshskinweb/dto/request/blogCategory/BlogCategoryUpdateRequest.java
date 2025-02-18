package com.kit.maximus.freshskinweb.dto.request.blogCategory;

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
public class BlogCategoryUpdateRequest implements Serializable {
    String blogCategoryName;
    String description;
    List<BlogEntity> blog;
}
