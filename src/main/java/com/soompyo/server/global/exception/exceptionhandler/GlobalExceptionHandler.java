package com.soompyo.server.global.exception.exceptionhandler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.soompyo.server.global.exception.BusinessException;
import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.global.response.ApiResponse.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(BusinessException e) {

        log.error("Business error {} happened: {}", e.getClass().getName(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.fail(e), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ErrorResponse>>> handleException(MethodArgumentNotValidException e) {
        log.error("Validation error {} happened: {}", e.getClass().getName(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.fail(e), e.getStatusCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(BadCredentialsException e) {
        log.error("Bad credentials error {} happened: {}", e.getClass().getName(), e.getMessage());

        return new ResponseEntity<>(ApiResponse.credentialFail(), HttpStatus.UNAUTHORIZED);
    }
}
