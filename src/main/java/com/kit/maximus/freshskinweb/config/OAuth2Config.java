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

    // Add Facebook fields
    public String getFacebookClientId() {
        return getEnvOrDefault("SPRING.SECURITY.OAUTH2.CLIENT.REGISTRATION.FACEBOOK.CLIENT-ID", "DEFAULT_FB_CLIENT_ID");
    }

    public String getFacebookClientSecret() {
        return getEnvOrDefault("SPRING.SECURITY.OAUTH2.CLIENT.REGISTRATION.FACEBOOK.CLIENT-SECRET", "DEFAULT_FB_CLIENT_SECRET");
    }

    public String getFacebookScope() {
        return getEnvOrDefault("SPRING.SECURITY.OAUTH2.CLIENT.REGISTRATION.FACEBOOK.SCOPE", "public_profile,email");
    }

    public String getFacebookRedirectUri() {
        String envValue = getEnvOrDefault("FACEBOOK_REDIRECT_URI", null);
        if (envValue == null || envValue.isEmpty()) {
            envValue = "http://localhost:8080/login/oauth2/code/facebook";
        }
        return envValue;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleRegistration = ClientRegistration.withRegistrationId("google")
                .clientId(getEnvOrDefault("GOOGLE_CLIENT_ID", "default"))
                .clientSecret(getEnvOrDefault("GOOGLE_CLIENT_SECRET", "default"))
                .scope("email", "profile")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(getEnvOrDefault("GOOGLE_REDIRECT_URI", "http://localhost:8080/login/oauth2/code/google"))
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();

        ClientRegistration facebookRegistration = ClientRegistration.withRegistrationId("facebook")
                .clientId(getEnvOrDefault("FACEBOOK_CLIENT_ID", "default"))
                .clientSecret(getEnvOrDefault("FACEBOOK_CLIENT_SECRET", "default"))
                .scope("public_profile")  // Chỉ lấy thông tin công khai, không cần email
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(getFacebookRedirectUri())
                .authorizationUri("https://www.facebook.com/v16.0/dialog/oauth")
                .tokenUri("https://graph.facebook.com/v16.0/oauth/access_token")
                .userInfoUri("https://graph.facebook.com/v16.0/me?fields=id,name,picture")  // Bỏ email
                .userNameAttributeName("id")
                .clientName("Facebook")
                .build();

        return new InMemoryClientRegistrationRepository(
                List.of(googleRegistration, facebookRegistration)
        );
    }

    @PostConstruct
    public void init() {
        System.out.println("getFacebookRedirectUri" + getFacebookRedirectUri());
    }

}
