package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kit.maximus.freshskinweb.utils.TypeUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
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
    String phone;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<OrderEntity> orders  = new ArrayList<>();

    public void createOrder(OrderEntity order) {
            orders.add(order);
            order.setUser(this);
    }

    public void removeOrder(OrderEntity order) {
        orders.remove(order);
        order.setUser(null);
    }





//    String roleId;
//    FOREIGN KEY (RoleID) REFERENCES Role(RoleID) ON DELETE CASCADE ON UPDATE CASCADE


}
