package com.soompyo.server.user.dto.response;

import java.time.LocalDate;

public record UserSignUpResponseDto(String email, LocalDate createdAt) {

}
