package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    Long roleId;

    @Column(name = "Title")
    String title;

    @Column(name = "Description", columnDefinition = "MEDIUMTEXT")
    String description;
    @Column(name = "Permission")
    String permission;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<UserEntity> user = new ArrayList<>();



}
