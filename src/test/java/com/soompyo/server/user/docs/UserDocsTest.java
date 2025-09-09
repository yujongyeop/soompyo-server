package com.soompyo.server.user.docs;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.soompyo.server.global.domain.UserRole;
import com.soompyo.server.global.security.CustomUserDetails;
import com.soompyo.server.global.security.JwtTokenProvider;
import com.soompyo.server.user.UserApiTest;
import com.soompyo.server.user.domain.User;
import com.soompyo.server.user.domain.UserStatus;
import com.soompyo.server.user.dto.response.UserDetailResponseDto;
import com.soompyo.server.user.service.UserService;

/**
 * REST Docs tests for non-auth user APIs.
 * - GET   /api/v1/users/me
 * - PATCH /api/v1/users/me/password
 * - DELETE /api/v1/users/me
 */
@UserApiTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class UserDocsTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private UserService userService;

    private static HeadersModifyingOperationPreprocessor maskAuthorizationHeader() {
        return modifyHeaders().set("Authorization", "Bearer {accessToken}");
    }

    private String getAccessToken() {
        User testUser = User.builder().email("test@test.com").role(UserRole.USER).build();
        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_USER");
        return jwtTokenProvider.generateAccessToken(new CustomUserDetails(testUser, List.of(role)));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void 사용자_내정보_조회() throws Exception {
        UserDetailResponseDto dto = UserDetailResponseDto
            .builder()
            .email("test@test.com")
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.parse("2024-01-01T00:00:00"))
            .updatedAt(LocalDateTime.parse("2024-01-02T13:40:58"))
            .lastLoginAt(LocalDateTime.parse("2024-01-02T13:40:58"))
            .build();
        given(userService.getUserByEmail("test@test.com")).willReturn(dto);

        mockMvc.perform(
                get("/api/v1/users/me")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(
                document("users-me-get",
                    preprocessRequest(prettyPrint(), maskAuthorizationHeader()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}")
                    ),
                    responseFields(
                        fieldWithPath("code").description("HTTP 상태 코드"),
                        fieldWithPath("status").description("HTTP 상태"),
                        fieldWithPath("message").description("결과 메시지"),
                        fieldWithPath("data.email").description("회원 이메일"),
                        fieldWithPath("data.role").description("회원 권한"),
                        fieldWithPath("data.status").description("회원 상태"),
                        fieldWithPath("data.createdAt").description("회원 생성 일시 (DateTime)"),
                        fieldWithPath("data.updatedAt").description("회원 수정 일시 (DateTime)"),
                        fieldWithPath("data.lastLoginAt").description("회원 마지막 로그인 일시 (DateTime)")
                    )
                )
            );
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void 사용자_비밀번호_변경() throws Exception {
        String requestBody = """
            {
              "currentPassword": "PastPassword123",
              "newPassword": "NewPassword456"
            }
            """;

        mockMvc.perform(
                patch("/api/v1/users/me/password")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(
                document("users-me-password-patch",
                    preprocessRequest(prettyPrint(), maskAuthorizationHeader()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}")
                    ),
                    requestFields(
                        fieldWithPath("currentPassword").description("현재 비밀번호"),
                        fieldWithPath("newPassword").description("새 비밀번호")
                    ),
                    responseFields(
                        fieldWithPath("code").description("HTTP 상태 코드"),
                        fieldWithPath("status").description("HTTP 상태"),
                        fieldWithPath("message").description("결과 메시지"),
                        fieldWithPath("data").ignored()
                    )
                )
            );
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void 사용자_탈퇴() throws Exception {
        mockMvc.perform(
                delete("/api/v1/users/me")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(
                document("users-me-delete",
                    preprocessRequest(prettyPrint(), maskAuthorizationHeader()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}")
                    ),
                    responseFields(
                        fieldWithPath("code").description("HTTP 상태 코드"),
                        fieldWithPath("status").description("HTTP 상태"),
                        fieldWithPath("message").description("결과 메시지"),
                        fieldWithPath("data").ignored()
                    )
                )
            );
    }
}
