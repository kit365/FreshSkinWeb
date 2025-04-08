package com.kit.maximus.freshskinweb.dto.request.skin_answer;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreationSkinAnswerRequest {
    Long id;
    String option;
    Long score;

}
