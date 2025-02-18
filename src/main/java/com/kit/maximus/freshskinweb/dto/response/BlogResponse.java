package com.kit.maximus.freshskinweb.dto.response;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogResponse extends AbstractEntity {
    Long blogId;
    String title;
    BlogCategoryEntity blogCategoryEntity;
    String content;
    String thumbnail;
    int position;
    boolean featured;
}
