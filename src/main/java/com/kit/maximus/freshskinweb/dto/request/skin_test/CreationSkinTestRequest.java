package com.kit.maximus.freshskinweb.dto.request.skin_test;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreationSkinTestRequest {

    Long userEntity;

    Long skinType;

    String questionGroup;

    String notes;

}
