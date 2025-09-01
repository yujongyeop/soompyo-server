package com.soompyo.server.user;

import static com.soompyo.server.user.TestDataGenerator.*;

import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.soompyo.server.global.ApiResponseType;
import com.soompyo.server.global.response.ApiResponse;
import com.soompyo.server.user.dto.request.UserLoginRequestDto;
import com.soompyo.server.user.dto.request.UserPasswordUpdateRequestDto;
import com.soompyo.server.user.dto.request.UserSignUpRequestDto;
import com.soompyo.server.user.dto.response.UserLoginResponseDto;
import com.soompyo.server.user.repository.UserRepository;

public record UserApiFixture(TestRestTemplate client, UserRepository userRepository) {

    public static final String SIGN_UP_URI = "/api/v1/users/signUp";
    public static final String LOG_IN_URI = "/api/v1/users/login";
    public static final String DELETE_USER_URI = "/api/v1/users/me";
    public static final String PASSWORD_UPDATE_URI = "/api/v1/users/me/password";

    public static UserApiFixture create(Environment environment, UserRepository userRepository) {
        TestRestTemplate client = new TestRestTemplate(new RestTemplateBuilder());
        LocalHostUriTemplateHandler uriTemplateHandler = new LocalHostUriTemplateHandler(environment);
        client.setUriTemplateHandler(uriTemplateHandler);
        return new UserApiFixture(client, userRepository);
    }

    private static <T> HttpEntity<T> convertToHttpEntity(T dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(dto, headers);
    }

    private boolean setAccessToken(String token) {
        return client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        });
    }

    public <T> ResponseEntity<ApiResponse<T>> signUpUser(UserSignUpRequestDto dto,
        ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        return client.exchange(SIGN_UP_URI, HttpMethod.POST, new HttpEntity<>(dto), typeRef);
    }

    public <T> ResponseEntity<ApiResponse<T>> loginUser(UserLoginRequestDto dto,
        ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        return client.exchange(LOG_IN_URI, HttpMethod.POST, convertToHttpEntity(dto), typeRef);
    }

    public ResponseEntity<ApiResponse<UserLoginResponseDto>> signUpAndLoginUser() {
        String email = generateEmail();
        String password = generatePassword();
        UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(email, password);
        signUpUser(userSignUpRequestDto, ApiResponseType.VOID);
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto(email, password);
        ResponseEntity<ApiResponse<UserLoginResponseDto>> response = loginUser(userLoginRequestDto,
            ApiResponseType.LOGIN_DTO);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String token = response.getBody().getData().accessToken();
            setAccessToken(token);
        }
        return response;
    }

    public ResponseEntity<ApiResponse<UserLoginResponseDto>> signUpAndLoginUser(String email, String password) {
        UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(email, password);
        signUpUser(userSignUpRequestDto, ApiResponseType.VOID);
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto(email, password);
        ResponseEntity<ApiResponse<UserLoginResponseDto>> response = loginUser(userLoginRequestDto,
            ApiResponseType.LOGIN_DTO);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String token = response.getBody().getData().accessToken();
            setAccessToken(token);
        }
        return response;
    }

    public ResponseEntity<Void> deleteUser() {
        return client.exchange(DELETE_USER_URI, HttpMethod.DELETE, null, Void.class);
    }

    public <T> ResponseEntity<ApiResponse<T>> updateUserPassword(UserPasswordUpdateRequestDto dto,
        ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        return client.exchange(PASSWORD_UPDATE_URI, HttpMethod.PATCH, convertToHttpEntity(dto), typeRef);
    }
}
