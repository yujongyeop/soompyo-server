package com.soompyo.server.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPasswordUpdateRequestDto(
    @NotBlank(message = "현재 비밀번호는 필수입니다.") @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 영문자와 숫자를 포함해야 합니다.") String currentPassword,
    @NotBlank(message = "새로운 비밀번호는 필수입니다.") @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 영문자와 숫자를 포함해야 합니다.") String newPassword) {
}
