package com.kit.maximus.freshskinweb.presentation.dto.request.skin_test;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SkinTestRequest {
    Long user;

    Long questionGroup;

    Long totalScore;

}
