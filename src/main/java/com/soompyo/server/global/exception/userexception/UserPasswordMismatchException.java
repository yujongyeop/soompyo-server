package com.soompyo.server.global.exception.userexception;

import org.springframework.http.HttpStatus;

import com.soompyo.server.global.exception.BusinessException;

public class UserPasswordMismatchException extends BusinessException {
    public static final String MESSAGE = "현재 비밀번호가 일치하지 않습니다.";
    public static final String CODE = "MEMBER-400";

    public UserPasswordMismatchException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
