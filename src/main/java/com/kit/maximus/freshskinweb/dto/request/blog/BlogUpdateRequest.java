package com.kit.maximus.freshskinweb.dto.request.blog;

import com.kit.maximus.freshskinweb.entity.BlogCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogUpdateRequest {
    String title;
    BlogCategory blogCategory;
    String content;
    String thumbnail;
    int position;
    boolean featured;
}
