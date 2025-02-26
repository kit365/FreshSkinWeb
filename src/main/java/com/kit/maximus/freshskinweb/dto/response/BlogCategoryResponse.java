package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogCategoryResponse implements Serializable {

    Long id;
    String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> image;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;
    String slug;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer position;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean deleted;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean featured;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("blog_ids")
    List<Long> blogID;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<BlogResponse> blogs;

}
