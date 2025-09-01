package com.soompyo.server.user.integrationtest;

import static com.soompyo.server.user.TestDataGenerator.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.soompyo.server.global.ApiResponseType;
import com.soompyo.server.global.exception.userexception.UserLogInInformationMismatchException;
import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.UserApiFixture;
import com.soompyo.server.user.UserApiTest;
import com.soompyo.server.user.dto.request.UserLoginRequestDto;
import com.soompyo.server.user.dto.request.UserSignUpRequestDto;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.dto.response.UserSignUpResponseDto;

@UserApiTest
@DisplayName("사용자 생성(회원가입 및 로그인) API - POST /api/v1/users/signUp & /api/v1/users/login")
class HttpCreateTest {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String generatedEmail = generateEmail();
        UserSignUpRequestDto dto = new UserSignUpRequestDto(generatedEmail, generatePassword());

        // Act
        ResponseEntity<ApiResponse<UserSignUpResponseDto>> response = fixture.signUpUser(dto,
            ApiResponseType.SIGNUP_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApiResponse<UserSignUpResponseDto> wrapper = response.getBody();
        assertThat(wrapper).isNotNull();

        UserSignUpResponseDto data = wrapper.getData();
        assertThat(data.email()).isEqualTo(generatedEmail);
    }

    @Test
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        UserSignUpRequestDto dto = new UserSignUpRequestDto(null, generatePassword());

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.signUpUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "invalid-email@", "invalid-email@test", "invalid-email@test.",
        "invalid-email@.com"})
    void email_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String email, @Autowired UserApiFixture fixture) {
        // Arrange
        UserSignUpRequestDto dto = new UserSignUpRequestDto(email, generatePassword());

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.signUpUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        UserSignUpRequestDto dto = new UserSignUpRequestDto(generateEmail(), null);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.signUpUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "word", "pass", "1234pas", "pass123", "password", "pass word",})
    void password_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String password, @Autowired UserApiFixture fixture) {
        // Arrange
        UserSignUpRequestDto dto = new UserSignUpRequestDto(generateEmail(), password);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.signUpUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void email_속성에_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        UserSignUpRequestDto firstRequestDto = new UserSignUpRequestDto(email, generatePassword());
        UserSignUpRequestDto secondRequestDto = new UserSignUpRequestDto(email, generatePassword());
        fixture.signUpUser(firstRequestDto, ApiResponseType.VOID);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.signUpUser(secondRequestDto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    /*
     * 로그인 테스트
     */

    @Test
    void 등록된_사용자가_로그인하면_200_OK_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        fixture.signUpUser(new UserSignUpRequestDto(email, password), ApiResponseType.VOID);

        // Act
        ResponseEntity<ApiResponse<UserLoginResponseDto>> response = fixture.loginUser(
            new UserLoginRequestDto(email, password), ApiResponseType.LOGIN_DTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApiResponse<UserLoginResponseDto> wrapper = response.getBody();
        assertThat(wrapper).isNotNull();

        UserLoginResponseDto data = wrapper.getData();
        assertThat(data).isNotNull();
        assertThat(data.email()).isEqualTo(email);
        assertThat(data.accessToken()).isNotBlank();
    }

    @Test
    void 로그인_요청시_이메일과_비밀번호_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        UserLoginRequestDto dto = new UserLoginRequestDto(null, null);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.loginUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void 로그인_요청시_이메일_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        UserLoginRequestDto dto = new UserLoginRequestDto(null, generatePassword());

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.loginUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 로그인_요청시_비밀번호_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        UserLoginRequestDto dto = new UserLoginRequestDto(generateEmail(), null);

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.loginUser(dto, ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 잘못된_비밀번호로_로그인하면_401_UNAUTHORIZED_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        fixture.signUpUser(new UserSignUpRequestDto(email, password), ApiResponseType.VOID);
        String invalidPassword = generatePassword();

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.loginUser(new UserLoginRequestDto(email, invalidPassword),
            ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ApiResponse<Void> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo(UserLogInInformationMismatchException.MESSAGE);
    }

    @Test
    void 등록되지_않은_사용자가_로그인하면_401_UNAUTHORIZED_상태코드를_반환한다(@Autowired UserApiFixture fixture) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        // Act
        ResponseEntity<ApiResponse<Void>> response = fixture.loginUser(new UserLoginRequestDto(email, password),
            ApiResponseType.VOID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ApiResponse<Void> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo(UserLogInInformationMismatchException.MESSAGE);
    }

}
