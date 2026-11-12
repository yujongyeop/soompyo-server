package com.soompyo.server.global.response;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

import com.soompyo.server.global.exception.BusinessException;
import com.soompyo.server.global.exception.userexception.UserLogInInformationMismatchException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.of(data, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.of(null, HttpStatus.NO_CONTENT);
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus httpStatus) {
        return ApiResponse.of(data, httpStatus.getReasonPhrase());
    }

    public static <T> ApiResponse<T> of(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    public static ApiResponse<Void> validationFail(BusinessException exception) {
        return new ApiResponse<>(null, exception.getMessage());
    }

    public static ApiResponse<List<ValidationErrorResponse>> validationFail(BindException exception) {
        return new ApiResponse<>(ValidationErrorResponse.of(exception.getFieldErrors()), "입력 값을 확인해주세요.");
    }

    public static ApiResponse<Void> credentialFail() {
        UserLogInInformationMismatchException exception = new UserLogInInformationMismatchException();
        return new ApiResponse<>(null, exception.getMessage());
    }
}
