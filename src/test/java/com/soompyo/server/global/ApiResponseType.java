package com.soompyo.server.global;

import org.springframework.core.ParameterizedTypeReference;

import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.dto.response.UserSignUpResponseDto;

public final class ApiResponseType {
    public static final ParameterizedTypeReference<ApiResponse<Void>> VOID = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<ApiResponse<UserSignUpResponseDto>> SIGNUP_DTO = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<ApiResponse<UserLoginResponseDto>> LOGIN_DTO = new ParameterizedTypeReference<>() {
    };

    private ApiResponseType() {
    }
}