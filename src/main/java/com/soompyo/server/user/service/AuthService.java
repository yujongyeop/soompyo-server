package com.soompyo.server.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soompyo.server.global.domain.UserRole;
import com.soompyo.server.global.exception.userexception.UserAlreadyExistException;
import com.soompyo.server.global.security.CustomUserDetails;
import com.soompyo.server.global.security.JwtTokenProvider;
import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.domain.UserStatus;
import com.soompyo.server.user.dto.request.UserLoginRequestDto;
import com.soompyo.server.user.dto.request.UserSignUpRequestDto;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.dto.response.UserSignUpResponseDto;
import com.soompyo.server.user.mapper.UserMapper;
import com.soompyo.server.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private void validateUniqueEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserAlreadyExistException();
        });
    }

    private String generateToken(CustomUserDetails principal) {
        return jwtTokenProvider.generateAccessToken(principal);
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

    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(),
            dto.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();
        String token = generateToken(principal);
        return userMapper.toLoginResponseDto(principal.getUser(), token);
    }

}
