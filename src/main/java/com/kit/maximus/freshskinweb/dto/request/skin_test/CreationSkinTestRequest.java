package com.kit.maximus.freshskinweb.dto.request.skin_test;

import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.SkinTypeEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreationSkinTestRequest extends AbstractEntity {

    Long userEntity;

    Long skinType;

    String notes;

    Date date;

}
