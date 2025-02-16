package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(name = "rolename")
    String roleName;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "permission")
    String permission;
}
