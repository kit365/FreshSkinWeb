package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.*;
import com.kit.maximus.freshskinweb.utils.RoleEnum;
import com.kit.maximus.freshskinweb.utils.TypeUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "User")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reviews", "orders"})
public class UserEntity extends AbstractEntity implements UserDetails {
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

    @Column(name = "Token", nullable = false, unique = true)
    private String token = UUID.randomUUID().toString();

    @Column(name = "Address")
    String address;

    //    TypeUser VARCHAR(10) DEFAULT 'Normal' CHECK (TypeUser IN ('Normal', 'VIP')),
    @Enumerated(EnumType.STRING)
    @Column(name = "Type_user")
    TypeUser typeUser = TypeUser.NORMAL;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<OrderEntity> orders  = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "role_id")
    RoleEntity role;

//    RoleEnum roleEnum;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    List<ReviewEntity> reviews = new ArrayList<>();

    public void createOrder(OrderEntity order) {
            orders.add(order);
            order.setUser(this);
    }

    public void removeOrder(OrderEntity order) {
        orders.remove(order);
        order.setUser(null);
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        return authorities;
    }

//    @Override
//    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//    @JsonSubTypes({@Type(value = SimpleGrantedAuthority.class, name = "SimpleGrantedAuthority")})
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(this.role.toString()));
//        return authorities;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }


//    String roleId;
//    FOREIGN KEY (RoleID) REFERENCES Role(RoleID) ON DELETE CASCADE ON UPDATE CASCADE


}
