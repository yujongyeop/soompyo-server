package com.soompyo.server.user.docs;

import static com.soompyo.server.user.TestDataGenerator.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.soompyo.server.ApiTest;

@ApiTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class AuthDocsTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void 회원가입_성공() throws Exception {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users/signUp").contentType(APPLICATION_JSON)
                    .content("{\"email\":\"" + email + "\"" + ",\"password\":\"" + password + "\"}")
            )
            .andExpect(status().isOk())
            .andDo(
                document("auth-sign-up-success", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("비밀번호")),
                    responseFields(
                        fieldWithPath("code").description("HTTP 상태 코드"),
                        fieldWithPath("status").description("HTTP 상태"),
                        fieldWithPath("message").description("결과 메시지"),
                        fieldWithPath("data.email").description("회원 이메일"),
                        fieldWithPath("data.createdAt").description("회원 생성일")
                    )
                )
            );
    }

    @Test
    void 회원가입_실패() throws Exception {
        // Arrange
        String email = generateEmail();
        String emptyPassword = "";

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users/signUp").contentType(APPLICATION_JSON)
                    .content("{\"email\":\"" + email + "\"" + ",\"password\":\"" + emptyPassword + "\"}")
            )
            .andExpect(status().isBadRequest())
            .andDo(
                document("auth-sign-up-fail", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("비밀번호")),
                    responseFields(
                        fieldWithPath("code").description("HTTP 상태 코드"),
                        fieldWithPath("status").description("HTTP 상태"),
                        fieldWithPath("message").description("결과 메시지"),
                        fieldWithPath("data").description("검증 에러 목록").type(JsonFieldType.ARRAY),
                        fieldWithPath("data[].field").description("에러 필드명"),
                        fieldWithPath("data[].value").description("거절된 값"),
                        fieldWithPath("data[].messages").description("에러 메시지 목록").type(JsonFieldType.ARRAY),
                        fieldWithPath("data[].messages[]").description("에러 메시지")
                    )
                )
            );
    }
}
