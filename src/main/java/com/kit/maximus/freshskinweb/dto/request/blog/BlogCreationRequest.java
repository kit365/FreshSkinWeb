package com.kit.maximus.freshskinweb.dto.request.blog;

import com.kit.maximus.freshskinweb.entity.BlogCategory;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogCreationRequest {
    String title;
    BlogCategory blogCategory;
    String content;
    String thumbnail;
    int position;
    boolean featured;
}
