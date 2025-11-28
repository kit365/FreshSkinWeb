package com.kit.maximus.freshskinweb.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.key-secret}")
    private String secretKey;

    @Value("${jwt.key-expiration-ms}")
    private int jwtExpirationInMs;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private int refreshTokenExpirationInMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities())
                .setExpiration(expiryDate)
                .setIssuedAt(now)
                .signWith(key)
                .compact();
    }

    public String generateTokenByUserEntity(com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity userEntity) {
        CustomUserDetails userDetails = new CustomUserDetails(userEntity);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities())
                .setExpiration(expiryDate)
                .setIssuedAt(now)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // ký token bằng key
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationInMs);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("tokenType", "REFRESH")
                .setExpiration(expiryDate)
                .setIssuedAt(now)
                .signWith(key)
                .compact();
    }

    public String generateRefreshTokenByUserEntity(com.kit.maximus.freshskinweb.dataaccess.entity.UserEntity userEntity) {
        CustomUserDetails userDetails = new CustomUserDetails(userEntity);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationInMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("tokenType", "REFRESH")
                .setExpiration(expiryDate)
                .setIssuedAt(now)
                .signWith(key)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // Kiểm tra xem token có phải là refresh token không
            String type = claims.get("tokenType", String.class);
            return "REFRESH".equals(type);
        } catch (JwtException | IllegalArgumentException e) {
        }
        return false;
    }
}
