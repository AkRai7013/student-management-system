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
            "infiuwefubufq9ubf9h340f3ifnionf2i3fi";

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    secretKey.getBytes(StandardCharsets.UTF_8)
            );


    // Generate JWT token
    public String generateToken(String email, String name) {

        return Jwts.builder()
                .subject(email)
                .claim("name", name)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60 * 10
                        )
                )
                .signWith(key)
                .compact();
    }


    // Get all claims from token
    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // Get email from token
    public String getEmailByToken(String token) {

        return getClaims(token).getSubject();
    }


    // Get name from token
    public String getNameByToken(String token) {

        return getClaims(token)
                .get("name", String.class);
    }
}