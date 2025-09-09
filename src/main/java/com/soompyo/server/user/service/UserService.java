package com.soompyo.server.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soompyo.server.global.exception.userexception.UserNotFoundException;
import com.soompyo.server.global.exception.userexception.UserPasswordMismatchException;
import com.soompyo.server.global.exception.userexception.UserPasswordUnchangedException;
import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.dto.request.UserPasswordUpdateRequestDto;
import com.soompyo.server.user.dto.response.UserDetailResponseDto;
import com.soompyo.server.user.mapper.UserMapper;
import com.soompyo.server.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public void softDeleteUser(String email) {
        User findedUser = userRepository.findActiveByEmail(email).orElseThrow(UserNotFoundException::new);
        findedUser.softDelete();
    }

    public UserDetailResponseDto getUserByEmail(String username) {
        User findedUser = userRepository.findActiveByEmail(username).orElseThrow(UserNotFoundException::new);
        return userMapper.toUserDetailDto(findedUser);
    }

    @Transactional
    public void updateUserPassword(String email, UserPasswordUpdateRequestDto dto) {
        User findedUser = userRepository.findActiveByEmail(email).orElseThrow(UserNotFoundException::new);
        if (!isPasswordMatch(dto.currentPassword(), findedUser.getPassword())) {
            throw new UserPasswordMismatchException();
        } else if (isPasswordMatch(dto.newPassword(), findedUser.getPassword())) {
            throw new UserPasswordUnchangedException();
        }
        findedUser.updatePassword(passwordEncoder.encode(dto.newPassword()));
    }
}
