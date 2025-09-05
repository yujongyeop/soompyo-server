package com.soompyo.server.global.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessExpMillis;
    private final String issuer;

    public JwtTokenProvider(String key, long accessExpMillis, String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.accessExpMillis = accessExpMillis * 60_000L;
        this.issuer = issuer;
    }

    public String generateAccessToken(CustomUserDetails principal) {
        Instant now = Instant.now();
        List<String> roles = principal.getAuthorities() == null ? List.of() :
            principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .issuer(issuer)
            .subject(String.valueOf(principal.getUser().getId()))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(accessExpMillis)))
            .claims(Map.of("email", principal.getUser().getEmail(), "roles", roles))
            .signWith(secretKey)
            .compact();
    }

    public Jws<Claims> parseAndValidate(String jwt) {
        return Jwts.parser().verifyWith(secretKey).requireIssuer(issuer).build().parseSignedClaims(jwt);
    }
}
