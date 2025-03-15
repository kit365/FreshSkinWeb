package com.kit.maximus.freshskinweb.config;

import jakarta.annotation.PostConstruct;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OAuth2Config {

    @Value("${GOOGLE_REDIRECT_URI:}")
    private String redirectUriFromProperties;

    private final Dotenv dotenv;

    public OAuth2Config() {
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = dotenv.get(key);
        }
        return value != null ? value : defaultValue;
    }

    public String getClientId() {
        return getEnvOrDefault("GOOGLE_CLIENT_ID", "DEFAULT_CLIENT_ID");
    }

    public String getClientSecret() {
        return getEnvOrDefault("GOOGLE_CLIENT_SECRET", "DEFAULT_CLIENT_SECRET");
    }

    public String getScope() {
        return getEnvOrDefault("GOOGLE_SCOPE", "email,profile");
    }

    public String getRedirectUri() {
        String envValue = getEnvOrDefault("GOOGLE_REDIRECT_URI", null);

        if (envValue == null || envValue.isEmpty()) {
            envValue = "http://localhost:8080/login/oauth2/code/google"; // Default cho local
        }

        System.out.println("✅ Redirect URI đang dùng: " + envValue);
        return envValue;
    }
//    @PostConstruct
//    public void init() {
//        System.out.println("🔎 GOOGLE_CLIENT_ID: " + getClientId());
//        System.out.println("🔎 GOOGLE_CLIENT_SECRET: " + getClientSecret());
//        System.out.println("🔎 GOOGLE_REDIRECT_URI: " + getRedirectUri());
//        System.out.println("🔎 GOOGLE_SCOPE: " + getScope());
//    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleRegistration = ClientRegistration.withRegistrationId("google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId(getClientId())
                .clientSecret(getClientSecret())
                .scope(getScope().split(","))
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub") // kha nang bi thieu field nay => khong tao duoc tai khoan googleRegistration
                .redirectUri(getRedirectUri())
                .clientName("Google")
                .build();

        return new InMemoryClientRegistrationRepository(googleRegistration);
    }

}
