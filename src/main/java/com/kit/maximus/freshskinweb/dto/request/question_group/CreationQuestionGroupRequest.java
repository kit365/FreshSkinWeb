package com.kit.maximus.freshskinweb.dto.request.question_group;

import com.kit.maximus.freshskinweb.dto.request.skin_questions.CreateSkinQuestionsRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import jakarta.persistence.Column;
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

    String groupName;

    String description;

    List<CreateSkinQuestionsRequest> skinQuestionsEntities;

    String status;

}
