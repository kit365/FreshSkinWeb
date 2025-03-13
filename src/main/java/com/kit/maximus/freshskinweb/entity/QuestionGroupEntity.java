package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "QuestionGroup")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionGroupEntity extends AbstractEntity{
    @Id
    @Column(name = "QuestionGroupId", nullable = false, updatable = false)
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "GroupName")
    String title;

    @Column(name = "Description")
    String description;

    @OneToMany(mappedBy = "questionGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<SkinQuestionsEntity> questions = new ArrayList<>();

    public void addSkinQuestionsEntity(SkinQuestionsEntity skinQuestionsEntity) {
        questions.add(skinQuestionsEntity);
        skinQuestionsEntity.setQuestionGroup(this);
    }

    public void removeSkinQuestionsEntity(SkinQuestionsEntity skinQuestionsEntity) {
        questions.remove(skinQuestionsEntity);
        skinQuestionsEntity.setQuestionGroup(null);
    }

}


