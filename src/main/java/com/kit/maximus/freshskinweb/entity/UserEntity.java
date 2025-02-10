package com.kit.maximus.freshskinweb.entity;

import com.kit.maximus.freshskinweb.utils.TypeUser;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "User")
public class UserEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID", insertable = false, updatable = false)
    Long id;


    @Column(name = "username", updatable = false, unique = true, nullable = false)
    String username;


    @Column(nullable = false)
    String password;

    String firstName;

    String lastName;

    @Column(unique = true)
    String email;

    String phonenumber;

    String avatar;

    String token;

    String address;

    //    TypeUser VARCHAR(10) DEFAULT 'Normal' CHECK (TypeUser IN ('Normal', 'VIP')),
    @Enumerated(EnumType.STRING)
    @Column(name = "type_user")
    TypeUser typeUser = TypeUser.NORMAL;


//    String roleId;
//    FOREIGN KEY (RoleID) REFERENCES Role(RoleID) ON DELETE CASCADE ON UPDATE CASCADE


}
