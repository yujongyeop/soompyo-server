package com.soompyo.server.global.exception.userexception;

import org.springframework.http.HttpStatus;

import com.soompyo.server.global.exception.BusinessException;

public class UserLogInInformationMismatchException extends BusinessException {
    public static final String MESSAGE = "사용자의 아이디 또는 비밀번호가 일치하지 않습니다.";
    public static final String CODE = "MEMBER-401";

    public UserLogInInformationMismatchException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
