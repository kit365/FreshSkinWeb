package com.kit.maximus.freshskinweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.maximus.freshskinweb.security.OAuth2AuthenticationSuccessHandler;
import com.kit.maximus.freshskinweb.service.users.AuthenticationService;
import com.kit.maximus.freshskinweb.service.users.CustomOAuth2UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



import javax.crypto.spec.SecretKeySpec;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableWebSecurity
public class SecurityConfig {

    Filter filter;

    AuthenticationService authenticationService;

    CustomOAuth2UserService customOAuth2UserService;

OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    OAuth2Config oAuth2Config;



    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;


    String[] ENDPOINT = {"admin/users/create", "auth/login"};


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oauth2AuthenticationSuccessHandler) // Thêm handler
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                )
                .userDetailsService(authenticationService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }

    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return customOAuth2UserService;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://project-swp391-n9j6.onrender.com", "http://localhost:8080", "http://localhost:3000","https://freshskinweb.onrender.com") // Hỗ trợ nhiều origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH")
                        .allowedHeaders("*")
                        .exposedHeaders("Set-Cookie")
                        .allowCredentials(true);
            }
        };
    }

}
