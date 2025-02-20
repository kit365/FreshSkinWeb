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
public class BlogCreationRequest implements Serializable {
    String title;
    String content;
    String thumbnail;
    Integer position;
    boolean featured;
    Long categoryID;
}
