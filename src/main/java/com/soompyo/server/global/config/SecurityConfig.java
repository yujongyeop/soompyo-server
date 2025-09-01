package com.soompyo.server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.soompyo.server.global.security.JwtAuthenticationFilter;
import com.soompyo.server.global.security.JwtTokenProvider;

@Configuration
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.access-exp-minutes}")
    private long accessExpMinutes;

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret, accessExpMinutes, "soompyo-api");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/api/v1/users/login")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/signUp")
                .permitAll()
                .anyRequest()
                .authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider()),
                UsernamePasswordAuthenticationFilter.class)
            .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
