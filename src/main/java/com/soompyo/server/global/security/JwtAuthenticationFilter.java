package com.soompyo.server.global.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider provider;

    public JwtAuthenticationFilter(JwtTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull FilterChain chain) throws
        ServletException,
        IOException {
        String auth = request.getHeader("Authorization");
        try {
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                Jws<Claims> claims = provider.parseAndValidate(token);
                String email = claims.getPayload().get("email", String.class);
                @SuppressWarnings("unchecked")
                List<String> roles = claims.getPayload().get("roles", List.class);

                List<SimpleGrantedAuthority> authorities = roles == null
                    ? List.of()
                    : roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, null,
                    authorities);
                SecurityContextHolder.getContext().setAuthentication(principal);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            logger.error("Authentication failed: " + e.getMessage());
        }
        chain.doFilter(request, response);
    }
}
