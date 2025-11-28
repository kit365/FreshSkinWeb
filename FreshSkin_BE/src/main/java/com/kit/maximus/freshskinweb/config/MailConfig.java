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

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private final String mailUsername = dotenv.get("MAIL_USERNAME");
    private final String mailPersonal = dotenv.get("MAIL_PERSONAL");

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(dotenv.get("MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(dotenv.get("MAIL_PORT")));
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(dotenv.get("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", dotenv.get("MAIL_SMTP_AUTH"));
        props.put("mail.smtp.starttls.enable", dotenv.get("MAIL_SMTP_STARTTLS"));
        props.put("mail.debug", "true");

        return mailSender;
    }
}
