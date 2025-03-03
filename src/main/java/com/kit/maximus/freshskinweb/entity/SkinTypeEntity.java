package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "skin_type")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkinTypeEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "SkinTypeID")
    Long id;

    @Column(name = "Type")
    String type;

    @Column(name = "Description",columnDefinition = "MEDIUMTEXT")
    String description;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "skinTypes" )
    @JsonBackReference
    List<ProductEntity> Products = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "skinType")
            @JsonManagedReference
    List<SkinTestEntity> skinTests = new ArrayList<>();
}
