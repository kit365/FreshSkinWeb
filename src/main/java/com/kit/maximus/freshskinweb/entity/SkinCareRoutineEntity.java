package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SkinCareRoutine")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SkinCareRoutineEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoutineID")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "SkinTypeId")
    SkinTypeEntity skinType;

    @Column(name = "Rountine",columnDefinition = "MEDIUMTEXT")
    String title;

    @Column(name = "description",columnDefinition = "MEDIUMTEXT")
    String description;

    @OneToMany(mappedBy = "skinCareRountine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<RountineStepEntity> rountineStep = new ArrayList<>();

}
