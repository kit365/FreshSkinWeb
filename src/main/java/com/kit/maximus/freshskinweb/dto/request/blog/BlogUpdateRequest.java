package com.kit.maximus.freshskinweb.dto.request.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    Long user;
    //    List<MultipartFile> thumbnail;
    Integer position;
    boolean featured;
    String status;
    boolean deleted;
    String author;
//    BlogCategoryResponse blogCategory;
    Long categoryID;
}
