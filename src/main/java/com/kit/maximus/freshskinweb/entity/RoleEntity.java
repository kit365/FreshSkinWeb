package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Role")
public class RoleEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "description",columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "permission")
    String permission;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "role", orphanRemoval = false)
    List<UserEntity> Users = new ArrayList<>();

    public void createUser(UserEntity userEntity){
        Users.add(userEntity);
        userEntity.setRole(this);
    }

    public void removeUser(UserEntity userEntity){
        Users.remove(userEntity);
        userEntity.setRole(null);
    }
}
