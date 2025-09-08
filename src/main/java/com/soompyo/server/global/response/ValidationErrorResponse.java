package com.soompyo.server.global.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;

public record ValidationErrorResponse(String field, String value, List<String> messages) {
    public static List<ValidationErrorResponse> of(List<FieldError> fieldErrors) {
        Map<String, List<FieldError>> grouped = fieldErrors.stream()
            .collect(Collectors.groupingBy(FieldError::getField, LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
            .map(entry -> {
                String field = entry.getKey();
                List<FieldError> errors = entry.getValue();
                String value = Optional.ofNullable(errors.getFirst().getRejectedValue()).orElse("null").toString();
                List<String> messages = errors.stream()
                    .map(fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value"))
                    .distinct()
                    .toList();
                return new ValidationErrorResponse(field, value, messages);
            })
            .toList();
    }
}
