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
    List<String> image;
    String description;
    String slug;
    Integer position;
    String status;
    boolean deleted;
    boolean featured;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("blog_ids")
    List<BlogResponse> blogID;

}
