package com.soompyo.server.user.integrationtest;

import static com.soompyo.server.user.TestDataGenerator.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.soompyo.server.global.ApiResponseType;
import com.soompyo.server.global.exception.userexception.UserPasswordMismatchException;
import com.soompyo.server.global.exception.userexception.UserPasswordUnchangedException;
import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.UserApiFixture;
import com.soompyo.server.user.UserApiTest;
import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.dto.request.UserPasswordUpdateRequestDto;
import com.soompyo.server.user.repository.UserRepository;

@UserApiTest
@DisplayName("사용자 수정 API - PATCH /api/v1/users/me/")
class HttpUpdateTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void 로그인_후_요청을_하면_200_OK_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        String currentPassword = generatePassword();
        fixture.signUpAndLoginUser(email, currentPassword);

        String newPassword = generatePassword();
        UserPasswordUpdateRequestDto dto = new UserPasswordUpdateRequestDto(currentPassword, newPassword);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.updateUserPassword(dto, ApiResponseType.VOID);

        User updatedUser = userRepository.findActiveByEmail(email).orElseThrow();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    void 로그인_하지_않고_요청하면_403_FORBIDDEN_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String currentPassword = generatePassword();
        String newPassword = generatePassword();
        UserPasswordUpdateRequestDto dto = new UserPasswordUpdateRequestDto(currentPassword, newPassword);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.updateUserPassword(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void 존재하지_않는_사용자가_요청하면_404_NOT_FOUND_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        String currentPassword = generatePassword();
        String newPassword = generatePassword();
        fixture.signUpAndLoginUser(email, currentPassword);
        fixture.deleteUser();

        UserPasswordUpdateRequestDto dto = new UserPasswordUpdateRequestDto(currentPassword, newPassword);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.updateUserPassword(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void 현재_비밀번호가_일치하지_않으면_400_BAD_REQUEST를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        fixture.signUpAndLoginUser(email, generatePassword());
        String invalidCurrentPassword = generatePassword();
        String newPassword = generatePassword();

        UserPasswordUpdateRequestDto dto = new UserPasswordUpdateRequestDto(invalidCurrentPassword, newPassword);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.updateUserPassword(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<Void> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo(UserPasswordMismatchException.MESSAGE);
    }

    @Test
    void 새_비밀번호가_현재_비밀번호와_동일하면_422_UNPROCESSABLE_ENTITY_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        String currentPassword = generatePassword();
        fixture.signUpAndLoginUser(email, currentPassword);

        UserPasswordUpdateRequestDto dto = new UserPasswordUpdateRequestDto(currentPassword, currentPassword);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.updateUserPassword(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        ApiResponse<Void> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo(UserPasswordUnchangedException.MESSAGE);
    }
}
