package com.kit.maximus.freshskinweb.business.serviceimpl;

import com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity;
import com.kit.maximus.freshskinweb.dataaccess.repository.UserRepository;
import com.kit.maximus.freshskinweb.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Check if account is deleted
        if(account.isDeleted()) {
            throw new UsernameNotFoundException("User account has been deleted: " + username);
        }

        return new CustomUserDetails(account);
    }
}
