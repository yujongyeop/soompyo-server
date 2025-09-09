package com.soompyo.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.soompyo.server.user.repository.UserRepository;

import com.soompyo.server.user.UserApiFixture;

public class FixtureConfiguration {

    @Bean
    @Scope("prototype")
    UserApiFixture userApiFixture(
        Environment environment,
        UserRepository userRepository
    ) {
        return UserApiFixture.create(environment, userRepository);
    }
}
