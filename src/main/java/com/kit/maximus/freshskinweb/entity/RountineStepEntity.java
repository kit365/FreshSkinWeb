package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RountineStep")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RountineStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RountineStepID")
    Long id;

    @Column(name = "position")
    Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RountineId")
    SkinCareRoutineEntity skinCareRountine;

    @Column(name = "Step",columnDefinition = "MEDIUMTEXT")
    String step;

    @Column(name = "Content",columnDefinition = "MEDIUMTEXT")
    String content;

    @OneToMany(mappedBy = "rountineStep", fetch = FetchType.LAZY)
            @OnDelete(action = OnDeleteAction.SET_NULL)
    List<ProductEntity> product = new ArrayList<>();
}
