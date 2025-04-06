package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RountineStepResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer position;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String step;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ProductResponseDTO> product;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean deleted;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date updatedAt;
}
