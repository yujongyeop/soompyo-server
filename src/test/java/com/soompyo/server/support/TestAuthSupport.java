package com.soompyo.server.support;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.soompyo.server.global.security.CustomUserDetails;
import com.soompyo.server.global.security.JwtTokenProvider;
import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.dto.request.UserSignUpRequestDto;
import com.soompyo.server.user.repository.UserRepository;
import com.soompyo.server.user.service.AuthService;

public class TestAuthSupport {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, AuthenticatedUser> cache = new ConcurrentHashMap<>();

    public TestAuthSupport(AuthService authService, UserRepository userRepository,
        JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthenticatedUser ensureUserWithToken(String email, String rawPassword) {
        return cache.compute(email, (key, existing) -> {
            if (existing != null) {
                return existing;
            }
            User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerUser(email, rawPassword));
            String token = generateToken(user);
            return new AuthenticatedUser(user, rawPassword, token);
        });
    }

    private User registerUser(String email, String rawPassword) {
        authService.signUp(new UserSignUpRequestDto(email, rawPassword));
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Failed to create user for tests: " + email));
    }

    private String generateToken(User user) {
        CustomUserDetails principal = new CustomUserDetails(user,
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
        return jwtTokenProvider.generateAccessToken(principal);
    }

    public void applyAuthentication(TestRestTemplate client, AuthenticatedUser user) {
        applyAuthentication(client, user.accessToken());
    }

    public void applyAuthentication(TestRestTemplate client, String accessToken) {
        List<ClientHttpRequestInterceptor> interceptors = client.getRestTemplate().getInterceptors();
        interceptors.removeIf(BearerAuthInterceptor.class::isInstance);
        interceptors.add(new BearerAuthInterceptor(accessToken));
    }

    public record AuthenticatedUser(User user, String rawPassword, String accessToken) {
    }

    private static class BearerAuthInterceptor implements ClientHttpRequestInterceptor {
        private final String token;

        private BearerAuthInterceptor(String token) {
            this.token = token;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body,
            ClientHttpRequestExecution execution) throws java.io.IOException {
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        }
    }
}
