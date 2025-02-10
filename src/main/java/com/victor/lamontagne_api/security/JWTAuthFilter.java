package com.victor.lamontagne_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/user/login",
            "/api/user/register"
    );

    public JWTAuthFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("\n=== JWTAuthFilter Debug Logs ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Headers: ");
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                System.out.println("  " + headerName + ": " + request.getHeader(headerName))
        );
        System.out.println("Auth Header: " + request.getHeader("Authorization"));
        System.out.println("================================\n");

        if (isPublicPath(request.getRequestURI(), request)) {
            System.out.println("Public path detected - passing through");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No/Invalid auth header - returning 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String userId = jwtService.validateToken(token);
            request.setAttribute("userId", userId);
            System.out.println("Token validation attempt");
            System.out.println("Token validated for user: " + userId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Authentication set in SecurityContext");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean isPublicPath(String requestURI, HttpServletRequest request) {
        return PUBLIC_PATHS.stream().anyMatch(requestURI::startsWith)
                || request.getMethod().equals("OPTIONS");
    }
}