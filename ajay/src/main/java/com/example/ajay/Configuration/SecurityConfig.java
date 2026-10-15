
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
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Public login endpoints
                        .requestMatchers(
                                HttpMethod.POST,
                                "/login/StudentLogin",
                                "/admin/login"
                        ).permitAll()

                        // Only ADMIN can create STAFF login accounts
                        .requestMatchers(
                                HttpMethod.POST,
                                "/admin/create"
                        ).hasRole("ADMIN")

                        // Admin APIs
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Student APIs
                        .requestMatchers("/students/**")
                        .hasAnyRole("ADMIN", "STAFF")

                        // Staff profile management: ADMIN only
                        .requestMatchers("/staff/**")
                        .hasRole("ADMIN")

                        // Holiday management
                        .requestMatchers(
                                HttpMethod.POST, "/holidays/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/holidays/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH, "/holidays/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/holidays/**")
                        .hasRole("ADMIN")

                        // Event management
                        .requestMatchers(
                                HttpMethod.POST, "/events/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/events/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH, "/events/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/events/**")
                        .hasRole("ADMIN")

                        // Notice management
                        .requestMatchers(
                                HttpMethod.POST, "/notices/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/notices/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH, "/notices/**")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/notices/**")
                        .hasRole("ADMIN")

                        // Admin and STAFF can read HRMS records.
                        // Services must enforce ownership.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/holidays/**",
                                "/events/**",
                                "/notices/**"
                        ).hasAnyRole("ADMIN", "STAFF")

                        // Token helper endpoints
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
