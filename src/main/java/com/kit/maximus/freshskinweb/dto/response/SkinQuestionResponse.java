package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkinQuestionResponse {
    Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String groupName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<QuestionGroupResponse> skinQuestionsEntities;

}
