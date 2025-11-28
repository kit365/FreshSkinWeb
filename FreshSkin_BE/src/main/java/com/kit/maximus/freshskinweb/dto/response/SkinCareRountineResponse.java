package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkinCareRountineResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    SkinTypeResponse skinType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<RountineStepResponse> rountineStep;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    boolean deleted;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date updatedAt;
}
