package com.kit.maximus.freshskinweb.dto.response;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogCategoryResponse implements Serializable {
    Long blogCategoryId;
    String blogCategoryName;
    String description;
    List<BlogEntity> blog;
}
