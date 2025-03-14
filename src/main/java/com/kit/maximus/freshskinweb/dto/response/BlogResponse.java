package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogResponse implements Serializable {

    Long id;
    String title;
    String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> thumbnail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer position;
    String slug;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean featured;
    String author;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean deleted;

    Date createdAt;

    Date updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BlogCategoryResponse blogCategory;


}
