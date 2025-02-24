package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder

public class ProductCategoryResponse implements Serializable {

    Long id;

    String title;

    String slug;

    String description;

    Integer position;

    List<String> image;

    boolean featured;

    String status;

    boolean deleted;

    List<ProductCategoryResponse> child;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("product_ids")
    List<Long> productIDs;

    // Chỉ áp dụng với danh mục cha
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String updatedAt;
}
