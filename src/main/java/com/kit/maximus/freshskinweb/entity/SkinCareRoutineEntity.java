package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "SkinTypeId", nullable = true)
    @JsonBackReference
    SkinTypeEntity skinTypeEntity;

    @Column(name = "MorningRoutine")
    String morningRoutine;

    @Column(name = "EveningRoutine")
    String eveningRoutine;

    @Column(name = "SpecialCare")
    String specialCare;

}
