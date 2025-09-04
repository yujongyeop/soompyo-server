package com.soompyo.server.user.integrationtest;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.UserApiFixture;
import com.soompyo.server.user.UserApiTest;

@UserApiTest
@DisplayName("사용자 삭제 API - DELETE /api/v1/users/me")
class HttpDeleteTest {

    @Test
    void 등록된_사용자를_삭제하면_204_No_Content_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        fixture.signUpAndLoginUser();

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.deleteUser();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 이미_삭제한_사용자를_삭제하면_404_Not_Found_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        fixture.signUpAndLoginUser();
        fixture.deleteUser();

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.deleteUser();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}