package com.kit.maximus.freshskinweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Permission")
public class PermissionEntity {

    @Id
    @Column(name = "Name")
    String name;
    @Column(name = "Description")
    String description;

}
