package com.kit.maximus.freshskinweb.dto.request.blog;

import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

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
    List<MultipartFile> thumbnail;
    Integer position;
    boolean featured;
    String status;
    Long categoryID;
}
