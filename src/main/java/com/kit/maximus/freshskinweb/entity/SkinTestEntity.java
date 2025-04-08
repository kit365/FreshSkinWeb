package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "SkinResult")
public class SkinTestEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductId")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "User_ID", nullable = true)
    @JsonBackReference
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
            @OnDelete(action = OnDeleteAction.SET_NULL)
            @JoinColumn(name = "questionGroupID", nullable = true)
    QuestionGroupEntity questionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "skinType_ID", nullable = true)
    @JsonBackReference
    SkinTypeEntity skinType;

    @Column(name = "notes", columnDefinition = "MEDIUMTEXT")
    String notes;

    @Column(name = "total_score")
    Long totalScore;

}
