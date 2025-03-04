package com.kit.maximus.freshskinweb.dto.request.skin_test;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdationSkinTestRequest {

    Long userEntity;

    Long skinType;

    String questionGroup;

    String notes;

}
