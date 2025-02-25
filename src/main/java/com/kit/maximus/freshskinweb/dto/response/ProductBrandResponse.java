package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class ProductBrandResponse implements Serializable {

    Long id;

    String title;

    String slug;

    String description;

    Integer position;

    List<String> image;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("product_ids")
    List<Long> productIDs;

    boolean featured;

    String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;
}
