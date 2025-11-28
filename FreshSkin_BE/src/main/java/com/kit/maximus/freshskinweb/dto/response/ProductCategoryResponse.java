package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String slug;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer position;
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    ProductCategoryResponse parent;

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
