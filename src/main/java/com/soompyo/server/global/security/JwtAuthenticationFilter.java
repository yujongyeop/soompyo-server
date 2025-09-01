package com.soompyo.server.global.security;

import java.io.IOException;
import java.util.List;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
        ServletException,
        IOException {
        String auth = request.getHeader("Authorization");
        try {
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                Jws<Claims> claims = provider.parseAndValidate(token);
                String email = claims.getPayload().get("email", String.class);
                String role = claims.getPayload().get("role", String.class);

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
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