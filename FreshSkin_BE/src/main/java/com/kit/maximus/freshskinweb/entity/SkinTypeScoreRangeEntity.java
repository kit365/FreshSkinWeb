package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "SkinTypeScoreRange")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkinTypeScoreRangeEntity extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SkinTypeScoreRangeId", insertable = false, updatable = false)
    Long id;

    @Column(name = "MinScore")
    Double minScore;

    @Column(name = "MaxScore")
    Double maxScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SkinTypeId")
    SkinTypeEntity skinType;

}
