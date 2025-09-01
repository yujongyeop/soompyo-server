package com.soompyo.server.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soompyo.server.global.domain.UserRole;
import com.soompyo.server.global.exception.userexception.UserAlreadyExistException;
import com.soompyo.server.global.exception.userexception.UserLogInInformationMismatchException;
import com.soompyo.server.global.exception.userexception.UserNotFoundException;
import com.soompyo.server.global.exception.userexception.UserPasswordMismatchException;
import com.soompyo.server.global.exception.userexception.UserPasswordUnchangedException;
import com.soompyo.server.global.security.JwtTokenProvider;
import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.domain.UserStatus;
import com.soompyo.server.user.dto.request.UserLoginRequestDto;
import com.soompyo.server.user.dto.request.UserPasswordUpdateRequestDto;
import com.soompyo.server.user.dto.request.UserSignUpRequestDto;
import com.soompyo.server.user.dto.response.UserDetailResponseDto;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.dto.response.UserSignUpResponseDto;
import com.soompyo.server.user.mapper.UserMapper;
import com.soompyo.server.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private String getAccessToken(User findedUser) {
        return jwtTokenProvider.generateAccessToken(findedUser.getId(), findedUser.getEmail(),
            findedUser.getRole().name());
    }

    private void validateUniqueEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserAlreadyExistException();
        });
    }

    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        User findedUser = userRepository.findActivateByEmail(dto.email())
            .orElseThrow(UserLogInInformationMismatchException::new);

        if (!isPasswordMatch(dto.password(), findedUser.getPassword())) {
            throw new UserLogInInformationMismatchException();
        }

        String token = getAccessToken(findedUser);
        findedUser.updateLastLoginAt();
        return userMapper.toLoginResponseDto(findedUser, token);
    }

    @Transactional
    public UserSignUpResponseDto signUp(UserSignUpRequestDto dto) {
        validateUniqueEmail(dto.email());

        User registerUser = User.builder()
            .email(dto.email())
            .password(passwordEncoder.encode(dto.password()))
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .build();

        User registeredUser = userRepository.join(registerUser);
        return userMapper.toRegisteredUserDto(registeredUser);

    }

    @Transactional
    public void softDeleteUser(String email) {
        User findedUser = userRepository.findActivateByEmail(email).orElseThrow(UserNotFoundException::new);
        findedUser.softDelete();
    }

    public UserDetailResponseDto getUserByEmail(String username) {
        User findedUser = userRepository.findActivateByEmail(username).orElseThrow(UserNotFoundException::new);
        return userMapper.toUserDetailDto(findedUser);
    }

    @Transactional
    public void updateUserPassword(String email, UserPasswordUpdateRequestDto dto) {
        User findedUser = userRepository.findActivateByEmail(email).orElseThrow(UserNotFoundException::new);
        if (!isPasswordMatch(dto.currentPassword(), findedUser.getPassword())) {
            throw new UserPasswordMismatchException();
        } else if (isPasswordMatch(dto.newPassword(), findedUser.getPassword())) {
            throw new UserPasswordUnchangedException();
        }
        findedUser.updatePassword(passwordEncoder.encode(dto.newPassword()));
    }
}
