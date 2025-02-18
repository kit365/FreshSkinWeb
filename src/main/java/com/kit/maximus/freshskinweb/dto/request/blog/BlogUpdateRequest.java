package com.kit.maximus.freshskinweb.dto.request.blog;

import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogUpdateRequest implements Serializable {
    String title;
    BlogCategoryEntity blogCategoryEntity;
    String content;
    String thumbnail;
    int position;
    boolean featured;
}
