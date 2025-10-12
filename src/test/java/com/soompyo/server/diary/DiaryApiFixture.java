package com.soompyo.server.diary;

import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;

import com.soompyo.server.diary.repository.DiaryRepository;
import com.soompyo.server.support.TestAuthSupport;
import com.soompyo.server.user.domain.User;

public record DiaryApiFixture(TestRestTemplate client, DiaryRepository diaryRepository,
                              TestAuthSupport authSupport) {
    private static final String DEFAULT_USER_EMAIL = "diary-test-user@test.com";
    private static final String DEFAULT_USER_PASSWORD = "Password1234";

    public static DiaryApiFixture create(Environment environment, DiaryRepository diaryRepository,
        TestAuthSupport authSupport) {
        TestRestTemplate client = new TestRestTemplate(new RestTemplateBuilder());
        LocalHostUriTemplateHandler uriTemplateHandler = new LocalHostUriTemplateHandler(environment);
        client.setUriTemplateHandler(uriTemplateHandler);
        return new DiaryApiFixture(client, diaryRepository, authSupport);
    }

    public TestAuthSupport.AuthenticatedUser ensureAuthenticatedDefaultUser() {
        TestAuthSupport.AuthenticatedUser authenticatedUser =
            authSupport.ensureUserWithToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD);
        authSupport.applyAuthentication(client, authenticatedUser);
        return authenticatedUser;
    }

    public User ensureTestUser() {
        return ensureAuthenticatedDefaultUser().user();
    }

}
