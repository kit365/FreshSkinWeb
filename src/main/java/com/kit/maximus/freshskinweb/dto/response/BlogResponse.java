package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogResponse implements Serializable {


    Long id;
    String title;
    String content;
    String thumbnail;
    Integer position;
    String slug;
    boolean featured;
    BlogCategoryResponse blogCategory;
    String status;

    boolean deleted;
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;
}
