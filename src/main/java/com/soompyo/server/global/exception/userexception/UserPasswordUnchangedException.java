package com.soompyo.server.global.exception.userexception;

import org.springframework.http.HttpStatus;

import com.soompyo.server.global.exception.BusinessException;

public class UserPasswordUnchangedException extends BusinessException {
    public static final String MESSAGE = "새 비밀번호가 현재 비밀번호와 동일합니다.";
    public static final String CODE = "MEMBER-422";

    public UserPasswordUnchangedException() {
        super(CODE, HttpStatus.UNPROCESSABLE_ENTITY, MESSAGE);
    }
}
