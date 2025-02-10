package com.victor.lamontagne_api.config;

import com.victor.lamontagne_api.security.JWTAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {
        UserDetailsServiceAutoConfiguration.class
})
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final JWTAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JWTAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        logger.info("=== SecurityConfig initialized ===");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("=== Configuring SecurityFilterChain ===");

        http
                .csrf(csrf -> {
                    csrf.disable();
                    logger.info("CSRF disabled");
                })
                .cors(cors -> {
                    cors.disable();
                    logger.info("CORS disabled");
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    logger.info("Session management set to STATELESS");
                })
                .authorizeHttpRequests(auth -> {
                    logger.info("Configuring request authorization");
                    auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    logger.info("OPTIONS requests permitted for all paths");
                    auth
                            .requestMatchers("/api/user/login", "/api/user/register").permitAll();
                    logger.info("Public paths configured: /api/user/login, /api/user/register");
                    auth
                            .anyRequest().authenticated();
                    logger.info("All other requests require authentication");
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        logger.info("JWT filter added before UsernamePasswordAuthenticationFilter");

        logger.info("=== SecurityFilterChain configuration completed ===");
        return http.build();
    }
}