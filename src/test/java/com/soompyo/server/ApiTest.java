package com.soompyo.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {ServerApplication.class,
    FixtureConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface ApiTest {
}
