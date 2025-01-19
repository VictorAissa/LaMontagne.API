package com.victor.lamontagne_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import com.victor.lamontagne_api.security.JWTService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JWTService jwtService;
    private final List<String> publicPaths = new ArrayList<>() {{
        add("/api/user/register");
        add("/api/user/login");
    }};

    @Autowired
    public SecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new OncePerRequestFilter() {
                            @Override
                            protected void doFilterInternal(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
                            ) throws ServletException, IOException {
                                System.out.println("Path: " + request.getRequestURI()); // debug
                                if (publicPaths.contains(request.getRequestURI())) {
                                    filterChain.doFilter(request, response);
                                    return;
                                }

                                String authHeader = request.getHeader("Authorization");
                                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    return;
                                }

                                try {
                                    String token = authHeader.substring(7);
                                    String userId = jwtService.validateToken(token);
                                    request.setAttribute("userId", userId);

                                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                                            userId, null, Collections.emptyList()
                                    );
                                    SecurityContextHolder.getContext().setAuthentication(authentication);

                                    filterChain.doFilter(request, response);
                                } catch (Exception e) {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                }
                            }
                        },
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
