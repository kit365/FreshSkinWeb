package com.kit.maximus.freshskinweb.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SkinAnswerResponse implements Serializable {

    Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String option;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long score;

}
