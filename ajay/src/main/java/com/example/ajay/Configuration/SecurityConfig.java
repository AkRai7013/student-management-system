package com.example.ajay.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTService jwtService;

    public SecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        JWTFilter jwtFilter = new JWTFilter(jwtService);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Public login endpoints
                        .requestMatchers(
                                HttpMethod.POST,
                                "/login/StudentLogin",
                                "/admin/login"
                        ).permitAll()

                        // Only ADMIN can create STAFF accounts
                        .requestMatchers(
                                HttpMethod.POST,
                                "/admin/create"
                        ).hasRole("ADMIN")

                        // All other Admin APIs require ADMIN
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Student APIs allow ADMIN and STAFF
                        .requestMatchers("/students/**")
                        .hasAnyRole("ADMIN", "STAFF")

                        // Authenticated token helper endpoints
                        .requestMatchers(
                                "/login/name",
                                "/login/email"
                        ).authenticated()

                        // All remaining endpoints require authentication
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