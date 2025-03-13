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
    @Column(name = "QuestionGroupID", nullable = false, updatable = false)
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "GroupName")
    String groupName;

    @Column(name = "Description")
    String description;

    @OneToMany(mappedBy = "questionGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
            @JsonManagedReference
    List<SkinQuestionsEntity> skinQuestionsEntities = new ArrayList<>();

    public void addSkinQuestionsEntity(SkinQuestionsEntity skinQuestionsEntity) {
        skinQuestionsEntities.add(skinQuestionsEntity);
        skinQuestionsEntity.setQuestionGroup(this);
    }

    public void removeSkinQuestionsEntity(SkinQuestionsEntity skinQuestionsEntity) {
        skinQuestionsEntities.remove(skinQuestionsEntity);
        skinQuestionsEntity.setQuestionGroup(null);
    }

}


