package com.kit.maximus.freshskinweb.entity;

import com.fasterxml.jackson.annotation.*;
import com.kit.maximus.freshskinweb.utils.SkinType;
import com.kit.maximus.freshskinweb.utils.TypeUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "User")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@With
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reviews", "orders"})
public class UserEntity extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", insertable = false, updatable = false)
    Long userID;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "avatar")
    List<String> avatar;

    @Column(name = "Token", nullable = false, unique = true)
    private String token = UUID.randomUUID().toString();

    @Column(name = "Address")
    String address;

    //    2 field này được lưu khi user đăng nhập bằng google
    @Column(name = "provider")
    private String provider; // GOOGLE, LOCAL, etc.

    @Column(name = "provider_id")
    private String providerId; // Google user ID

    //    TypeUser VARCHAR(10) DEFAULT 'Normal' CHECK (TypeUser IN ('Normal', 'VIP')),
    @Enumerated(EnumType.STRING)
    @Column(name = "Type_user")
    TypeUser typeUser = TypeUser.NORMAL;

    @Column(name = "skin_type")
    String skinType = SkinType.NORMAL.getVNESEname();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<OrderEntity> orders  = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "roleId", nullable = true)
    RoleEntity role;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    List<ReviewEntity> reviews = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    @JsonManagedReference
    List<SkinTestEntity> skinTests = new ArrayList<>();


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
        if (this.role != null) {
            authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // hoặc giá trị mặc định phù hợp
        }
        return authorities;
    }


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


}
