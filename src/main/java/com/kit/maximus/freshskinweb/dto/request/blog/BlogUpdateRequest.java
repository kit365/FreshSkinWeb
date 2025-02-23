package com.kit.maximus.freshskinweb.dto.request.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogUpdateRequest implements Serializable {
    String title;
    String content;
    String thumbnail;
    Integer position;
    boolean featured;
    String status;
    boolean deleted;
    BlogCategoryResponse blogCategory;

    Long categoryID;
}
