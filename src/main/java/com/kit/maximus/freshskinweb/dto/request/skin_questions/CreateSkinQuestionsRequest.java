package com.kit.maximus.freshskinweb.dto.request.skin_questions;

import com.kit.maximus.freshskinweb.dto.request.skin_answer.CreationSkinAnswerRequest;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateSkinQuestionsRequest implements Serializable {
    Long id;
    String questionText;
    String questionGroup;
    List<CreationSkinAnswerRequest> answers;
}
