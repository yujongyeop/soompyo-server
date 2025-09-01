package com.soompyo.server.global.exception.userexception;

import org.springframework.http.HttpStatus;

import com.soompyo.server.global.exception.BusinessException;

public class UserAlreadyExistException extends BusinessException {
    public static final String MESSAGE = "이미 존재하는 회원입니다.";
    public static final String CODE = "MEMBER-409";

    public UserAlreadyExistException() {
        super(CODE, HttpStatus.CONFLICT, MESSAGE);
    }
}
