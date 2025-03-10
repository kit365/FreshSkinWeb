package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "QuestionGroup")
public class QuestionGroupEntity {
    @Id
    @Column(name = "QuestionGroupID")
    String id;


}


