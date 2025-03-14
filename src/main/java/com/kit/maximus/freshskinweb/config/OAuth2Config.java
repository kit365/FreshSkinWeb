package com.kit.maximus.freshskinweb.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class OAuth2Config {

    private final Dotenv dotenv;

    public OAuth2Config() {
        // Load file .env nếu có (chỉ hoạt động ở local)
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
        return getEnvOrDefault("GOOGLE_CLIENT_SECRET", "DEFAULT_SECRET");
    }

    public String getRedirectUri() {
        return getEnvOrDefault("GOOGLE_REDIRECT_URI", "http://localhost:8080/login/oauth2/code/google");
    }

    public String getScope() {
        return getEnvOrDefault("GOOGLE_SCOPE", "email,profile");
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ GOOGLE_REDIRECT_URI: " + getRedirectUri());
    }
}
