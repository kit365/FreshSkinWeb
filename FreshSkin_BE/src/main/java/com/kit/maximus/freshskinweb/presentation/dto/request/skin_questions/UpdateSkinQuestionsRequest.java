package com.kit.maximus.freshskinweb.presentation.dto.request.skin_questions;

import com.kit.maximus.freshskinweb.dataaccess.entity.skins.SkinAnswerEntity;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateSkinQuestionsRequest implements Serializable {
    String questionText;
    String questionGroup;
    List<SkinAnswerEntity> skinAnswers;
}
