package com.kit.maximus.freshskinweb.dto.response;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkinQuestionsResponse extends AbstractEntity {
    Long id;
    String questionText;
    String questionGroup;

}
