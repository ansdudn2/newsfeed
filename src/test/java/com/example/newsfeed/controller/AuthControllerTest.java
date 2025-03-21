package com.example.newsfeed.controller;

import com.example.newsfeed.domain.auth.controller.AuthController;
import com.example.newsfeed.domain.auth.dto.request.LoginRequestDto;
import com.example.newsfeed.domain.auth.dto.request.SignupRequestDto;
import com.example.newsfeed.domain.auth.dto.response.LoginResponseDto;
import com.example.newsfeed.domain.auth.dto.response.SignupResponseDto;
import com.example.newsfeed.domain.auth.service.AuthService;
import com.example.newsfeed.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void 회원가입_성공() throws Exception {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto("test@example.com", "Password1!@", "testuser","");
        SignupResponseDto responseDto = new SignupResponseDto(1L, "test@example.com", "testuser", "회원가입이 완료되었습니다.");

        Mockito.when(authService.signup(Mockito.any(SignupRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    }

    @Test
    void 로그인_성공() throws Exception {
        // Given
        LoginRequestDto requestDto = new LoginRequestDto("test@example.com", "Password1!");
        LoginResponseDto responseDto = new LoginResponseDto("mock-jwt-token");

        Mockito.when(authService.login(Mockito.any(LoginRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void 로그아웃_성공() {
        // Given
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        String refreshToken = "dummy-refresh-token";
        Mockito.doNothing().when(authService).logout(Mockito.eq(1L), Mockito.anyString());

        // When
        ResponseEntity<String> response = authController.logout(dummyUser, refreshToken);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("로그아웃 성공", response.getBody());
    }

    @Test
    void 회원탈퇴_성공() {
        // Given
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        Mockito.doNothing().when(authService).withdraw(Mockito.eq(1L));

        // When
        ResponseEntity<String> response = authController.withdraw(dummyUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원탈퇴가 완료되었습니다.", response.getBody());
    }
}
