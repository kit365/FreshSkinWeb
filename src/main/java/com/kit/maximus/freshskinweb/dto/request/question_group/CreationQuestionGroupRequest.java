package com.kit.maximus.freshskinweb.dto.request.question_group;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
}
