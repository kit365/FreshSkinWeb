package com.kit.maximus.freshskinweb.entity;

import com.kit.maximus.freshskinweb.utils.TypeUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "User")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", insertable = false, updatable = false)
    Long id;


    @Column(name = "Username", updatable = false, unique = true, nullable = false)
    String username;


    @Column(name = "Password", nullable = false)
    String password;

    @Column(name = "FirstName")
    String firstName;

    @Column(name = "LastName")
    String lastName;

    @Column(name = "Email", unique = true)
    String email;

    @Column(name = "PhoneNumber")
    String phonenumber;

    @Column(name = "Avatar")
    String avatar;

    @Column(name = "Token")
    String token;

    @Column(name = "Address")
    String address;

    //    TypeUser VARCHAR(10) DEFAULT 'Normal' CHECK (TypeUser IN ('Normal', 'VIP')),
    @Enumerated(EnumType.STRING)
    @Column(name = "Type_user")
    TypeUser typeUser = TypeUser.NORMAL;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    List<OrderEntity> orders;


//    String roleId;
//    FOREIGN KEY (RoleID) REFERENCES Role(RoleID) ON DELETE CASCADE ON UPDATE CASCADE


}
