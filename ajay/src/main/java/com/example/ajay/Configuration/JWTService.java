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

    private final SecretKey key;

    public JWTService() {
        String secret = System.getenv("JWT_SECRET");

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable is not configured"
            );
        }

        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least 32 bytes long"
            );
        }

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String email, String name) {
        return generateToken(email, name, null);
    }

    public String generateToken(
            String email,
            String name,
            String role
    ) {
        var builder = Jwts.builder()
                .subject(email)
                .claim("email", email)
                .claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 1000L * 60 * 60 * 10
                ));

        if (role != null && !role.isBlank()) {
            builder.claim("role", role);
        }

        return builder
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

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