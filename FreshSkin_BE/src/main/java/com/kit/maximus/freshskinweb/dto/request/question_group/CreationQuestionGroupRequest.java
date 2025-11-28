package com.kit.maximus.freshskinweb.dto.request.question_group;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class CreationQuestionGroupRequest {
    String title;

    String description;

    List<CreateSkinQuestionsRequest> questions;

    String status;

}
