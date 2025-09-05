package com.soompyo.server.global.exception.security;

public class AuthenticationManagerInitializationException extends RuntimeException {
    public AuthenticationManagerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
