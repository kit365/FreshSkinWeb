package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SkinAnswer")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SkinAnswerEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SkinQuestionID")
    Long id;

    @Column(name = "SkinOption")
    String option;

    @Column(name = "AnswerScore")
    Long score;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "skinQuestionsEntity", nullable = true)
    @JsonBackReference
    SkinQuestionsEntity skinQuestionsEntity;

}
