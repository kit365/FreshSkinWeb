package com.kit.maximus.freshskinweb.dto.request.rountine_step;

import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreationRountineStepRequest {
    Integer position;
    String step;
    String content;
    String productCategory;
}
