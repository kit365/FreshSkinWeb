package com.kit.maximus.freshskinweb.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl;

        // Kiểm tra nếu biến môi trường CLOUDINARY_URL đã có
        if (System.getenv("CLOUDINARY_URL") != null) {
            cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        } else {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            cloudinaryUrl = dotenv.get("CLOUDINARY_URL");
        }

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            throw new RuntimeException("CLOUDINARY_URL is not set in environment variables or .env file.");
        }

        return new Cloudinary(cloudinaryUrl);
    }
}
