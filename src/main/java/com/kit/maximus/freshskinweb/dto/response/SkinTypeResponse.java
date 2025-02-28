package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkinTypeResponse implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("product_ids")
    List<Long> productID;
}
