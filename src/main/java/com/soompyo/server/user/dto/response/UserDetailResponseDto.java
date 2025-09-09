package com.soompyo.server.user.dto.response;

import java.time.LocalDateTime;

import com.soompyo.server.global.domain.UserRole;
import com.soompyo.server.user.domain.UserStatus;

import lombok.Builder;

@Builder
public record UserDetailResponseDto(String email, UserRole role, UserStatus status, LocalDateTime createdAt,
                                    LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
}
