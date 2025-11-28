package com.kit.maximus.freshskinweb.dto.request.blog;


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
public class BlogUpdateRequest implements Serializable {
    String title;
    String content;
    Long user;
    List<MultipartFile> newImg;
    List<String> thumbnail;
    boolean featured;
    Long categoryID;
    String status;

}
