package com.soompyo.server.global.response;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import com.soompyo.server.global.exception.BusinessException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private int code;
    private String status;
    private String message;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.of(data, HttpStatus.OK);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus httpStatus) {
        return ApiResponse.of(data, httpStatus, httpStatus.getReasonPhrase());
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus httpStatus, String message) {
        return new ApiResponse<>(data, httpStatus.value(), httpStatus.name(), message);
    }

    public static ApiResponse<Void> fail(BusinessException exception) {
        return new ApiResponse<>(null, exception.getHttpStatus().value(), exception.getErrorCode(),
            exception.getMessage());
    }

    public static ApiResponse<List<ErrorResponse>> fail(BindException exception) {
        return new ApiResponse<>(ErrorResponse.of(exception.getFieldErrors()), HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.name(), "입력 값을 확인해주세요.");
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {

        private String field;
        private String value;
        private String reason;

        public static List<ErrorResponse> of(List<FieldError> fieldErrors) {
            return fieldErrors.stream()
                .map(fieldError -> new ErrorResponse(fieldError.getField(), getRejectedValue(fieldError),
                    fieldError.getDefaultMessage()))
                .toList();
        }

        private static String getRejectedValue(FieldError fieldError) {
            return Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString();
        }

    }
}