package com.kit.maximus.freshskinweb.dataaccess.entity;

import com.kit.maximus.freshskinweb.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "ForgotPassword")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ForgotPasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ForgotPasswordId")
    Long id;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "otp", nullable = false)
    String OTP;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;

    @Column(name = "Deleted")
    boolean deleted;


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }
}
