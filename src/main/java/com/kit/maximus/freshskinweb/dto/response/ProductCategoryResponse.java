package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductCategoryResponse implements Serializable {

    Long id;

    String title;

    String slug;

    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer position;

    List<String> image;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean featured;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean deleted;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductCategoryResponse> child;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("product_ids")
    List<Long> productIDs;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductResponseDTO> products;

    // Chỉ áp dụng với danh mục cha
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String createdAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String updatedAt;
}
