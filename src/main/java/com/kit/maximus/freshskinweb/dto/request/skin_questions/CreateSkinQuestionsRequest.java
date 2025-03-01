package com.kit.maximus.freshskinweb.dto.request.skin_questions;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateSkinQuestionsRequest {
    String questionText;
    String questionGroup;
}
