package com.kit.maximus.freshskinweb.presentation.dto.request.question_group;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class UpdationQuestionGroupRequest {
    String title;

    String description;
}
