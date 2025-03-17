package com.kit.maximus.freshskinweb.service.users;

import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            UserEntity user = new UserEntity();
            user.setPassword(UUID.randomUUID().toString());
            user.setUsername(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setFirstName(name); // tạm thời
            user.setProvider("GOOGLE");
            user.setProviderId(providerId);
            userRepository.save(user);
            log.info("Saved user from GG login: " + user);
        }

        return oauth2User;
    }
}