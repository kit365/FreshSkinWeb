package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SkinQuestions")
@ToString
public class SkinQuestionsEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SkinQuestionID")
    Long id;

    @Column(name = "QuestionText")
    String questionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuestionGroupId", nullable = true)
    @JsonBackReference
    QuestionGroupEntity questionGroup;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "skinQuestionsEntity", orphanRemoval = true)
    @JsonManagedReference
    List<SkinAnswerEntity> answers = new ArrayList<>();

    public void addSkinAnswerEntity(SkinAnswerEntity skinAnswerEntity) {
        answers.add(skinAnswerEntity);
        skinAnswerEntity.setSkinQuestionsEntity(this);
    }

    public void removeSkinAnswerEntity(SkinAnswerEntity skinAnswerEntity) {
        answers.remove(skinAnswerEntity);
        skinAnswerEntity.setSkinQuestionsEntity(null);
    }
}
