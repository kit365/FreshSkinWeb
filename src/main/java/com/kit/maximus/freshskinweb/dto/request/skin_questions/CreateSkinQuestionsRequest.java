package com.kit.maximus.freshskinweb.dto.request.skin_questions;

import com.kit.maximus.freshskinweb.entity.SkinAnswerEntity;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateSkinQuestionsRequest implements Serializable {

    Integer questionNumber;
    String questionText;
    String questionGroup;

    List<SkinAnswerEntity> skinAnswers;
}
