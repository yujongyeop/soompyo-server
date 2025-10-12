package com.soompyo.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.soompyo.server.diary.DiaryApiFixture;
import com.soompyo.server.diary.repository.DiaryRepository;
import com.soompyo.server.global.security.JwtTokenProvider;
import com.soompyo.server.support.TestAuthSupport;
import com.soompyo.server.user.UserApiFixture;
import com.soompyo.server.user.repository.UserRepository;
import com.soompyo.server.user.service.AuthService;

public class FixtureConfiguration {

    @Bean
    @Scope("prototype")
    UserApiFixture userApiFixture(
        Environment environment,
        UserRepository userRepository
    ) {
        return UserApiFixture.create(environment, userRepository);
    }

    @Bean
    TestAuthSupport testAuthSupport(
        AuthService authService,
        UserRepository userRepository,
        JwtTokenProvider jwtTokenProvider
    ) {
        return new TestAuthSupport(authService, userRepository, jwtTokenProvider);
    }

    @Bean
    @Scope("prototype")
    DiaryApiFixture diaryApiFixture(
        Environment environment,
        DiaryRepository diaryRepository,
        TestAuthSupport testAuthSupport
    ) {
        return DiaryApiFixture.create(environment, diaryRepository, testAuthSupport);
    }
}
