package com.soompyo.server.user.integrationtest;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.soompyo.server.ApiTest;
import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.UserApiFixture;
import com.soompyo.server.user.repository.UserRepository;

@ApiTest
@DisplayName("사용자 조회 API - GET /api/v1/users")
class HttpReadTest {

    @BeforeEach
    void beforeEach(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
    }

    @Test
    void 로그인_하지않고_요청하면_403_FORBIDDEN_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.requestMyInfo();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void 로그인_후_요청하면_200_OK_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        fixture.signUpAndLoginUser();

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.requestMyInfo();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 사용자_삭제_후_조회를_시도하면_404_NOT_FOUND_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        fixture.signUpAndLoginUser();
        fixture.deleteUser();

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.requestMyInfo();

        // Assert
        ApiResponse<Void> body = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).isNotNull();
    }
}
