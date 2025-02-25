package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkinTypeResponse {

    long id;
    String type;
    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("product_ids")
    List<Long> productID;
}
