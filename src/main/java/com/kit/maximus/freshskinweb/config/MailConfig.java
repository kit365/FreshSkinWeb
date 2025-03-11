package com.kit.maximus.freshskinweb.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@PropertySource("classpath:application.properties")
@Configuration
@Getter
@Setter
public class MailConfig {
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        mailSender.setHost(dotenv.get("MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(dotenv.get("MAIL_PORT")));
        mailSender.setUsername(dotenv.get("MAIL_USERNAME"));
        mailSender.setPassword(dotenv.get("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", dotenv.get("MAIL_SMTP_AUTH"));
        props.put("mail.smtp.starttls.enable", dotenv.get("MAIL_SMTP_STARTTLS"));
        props.put("mail.debug", "true");

        return mailSender;
    }

//    @PostConstruct
//    public void logMailConfig() {
//        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//        System.out.println("=== Mail Configuration ===");
//        System.out.println("Host: " + dotenv.get("MAIL_HOST"));
//        System.out.println("Port: " + dotenv.get("MAIL_PORT"));
//        System.out.println("Username: " + dotenv.get("MAIL_USERNAME"));
//        System.out.println("Password length: " + (dotenv.get("MAIL_PASSWORD") != null ? dotenv.get("MAIL_PASSWORD").length() : 0));
//        System.out.println("Personal: " + dotenv.get("MAIL_PERSONAL"));
//        System.out.println("SMTP Auth: " + dotenv.get("MAIL_SMTP_AUTH"));
//        System.out.println("SMTP StartTLS: " + dotenv.get("MAIL_SMTP_STARTTLS"));
//        System.out.println("=======================");
//    }
}
