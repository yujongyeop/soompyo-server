package com.soompyo.server.user.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.dto.request.UserLoginRequestDto;
import com.soompyo.server.user.dto.request.UserPasswordUpdateRequestDto;
import com.soompyo.server.user.dto.request.UserSignUpRequestDto;
import com.soompyo.server.user.dto.response.UserDetailResponseDto;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.dto.response.UserSignUpResponseDto;
import com.soompyo.server.user.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserDetailResponseDto> getUser(Principal principal) {
        return ApiResponse.ok(userService.getUserByEmail(principal.getName()));
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> updateUserPassword(Principal principal,
        @RequestBody @Valid UserPasswordUpdateRequestDto dto) {
        userService.updateUserPassword(principal.getName(), dto);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(Principal principal) {
        userService.softDeleteUser(principal.getName());
        return ApiResponse.noContent();
    }

    @PostMapping("/signUp")
    public ApiResponse<UserSignUpResponseDto> signUp(@RequestBody @Valid UserSignUpRequestDto dto) {
        return ApiResponse.ok(userService.signUp(dto));
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponseDto> login(@RequestBody @Valid UserLoginRequestDto dto) {
        return ApiResponse.ok(userService.login(dto));
    }
}
