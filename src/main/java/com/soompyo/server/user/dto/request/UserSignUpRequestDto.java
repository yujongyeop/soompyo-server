package com.soompyo.server.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserSignUpRequestDto(
    @NotBlank(message = "이메일은 필수입니다.") @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.") String email,
    @NotBlank(message = "비밀번호는 필수입니다.") @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 영문자와 숫자를 포함해야 합니다.") String password) {
}
