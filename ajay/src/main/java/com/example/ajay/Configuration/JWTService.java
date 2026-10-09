package com.example.ajay.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    private final String secretKey =
            System.getenv("JWT_SECRET");

    private final SecretKey key = Keys.hmacShaKeyFor(
            secretKey != null
                    ? secretKey.getBytes(StandardCharsets.UTF_8)
                    : "replace-this-with-a-long-random-secret-key-123456"
                    .getBytes(StandardCharsets.UTF_8)
    );

    // Existing method for Student login
    public String generateToken(String email, String name) {

        return Jwts.builder()
                .subject(email)
                .claim("name", name)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 1000L * 60 * 60 * 10
                ))
                .signWith(key)
                .compact();
    }

    // New method for Admin/Staff login
    public String generateToken(
            String email,
            String name,
            String role) {

        return Jwts.builder()
                .subject(email)
                .claim("name", name)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 1000L * 60 * 60 * 10
                ))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailByToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getNameByToken(String token) {
        return getClaims(token).get("name", String.class);
    }

    public String getRoleByToken(String token) {
        return getClaims(token).get("role", String.class);
    }
}