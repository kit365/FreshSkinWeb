package com.kit.maximus.freshskinweb.security;

import com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;


public class CustomUserDetails implements UserDetails {

    private UserEntity account;
    private String roleName;

    public CustomUserDetails(UserEntity account) {
        this.account = account;
        if (account != null && account.getRole() != null) {
            this.roleName = account.getRole().getTitle();
        }
    }

    public CustomUserDetails() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleName != null && !roleName.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
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

    public UserEntity getAccount() {
        return account;
    }
}
