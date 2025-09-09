package com.soompyo.server.global.exception.userexception;

import org.springframework.http.HttpStatus;

import com.soompyo.server.global.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
    public static final String MESSAGE = "존재하지 않는 회원입니다.";
    public static final String CODE = "MEMBER-404";

    public UserNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
