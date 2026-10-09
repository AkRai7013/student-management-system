package com.example.ajay.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTService jwtService;

    public SecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        JWTFilter jwtFilter = new JWTFilter(jwtService);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints for local testing
                        .requestMatchers(
                                "/login/StudentLogin",
                                "/admin/create",
                                "/admin/login"
                        ).permitAll()

                        // Only ADMIN can access future admin-management endpoints
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // ADMIN and STAFF can access Student APIs
                        .requestMatchers("/students/**")
                        .hasAnyRole("ADMIN", "STAFF")

                        // Existing token-claim endpoints
                        .requestMatchers(
                                "/login/name",
                                "/login/email"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}