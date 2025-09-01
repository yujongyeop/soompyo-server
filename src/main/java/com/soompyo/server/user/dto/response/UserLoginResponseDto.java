package com.soompyo.server.user.dto.response;

public record UserLoginResponseDto(Long id, String email, String role, String accessToken) {
}
