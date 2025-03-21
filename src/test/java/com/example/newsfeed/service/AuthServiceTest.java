package com.example.newsfeed.service;

import com.example.newsfeed.config.JwtUtil;
import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.domain.auth.dto.request.LoginRequestDto;
import com.example.newsfeed.domain.auth.dto.request.SignupRequestDto;
import com.example.newsfeed.domain.auth.dto.response.LoginResponseDto;
import com.example.newsfeed.domain.auth.dto.response.SignupResponseDto;
import com.example.newsfeed.domain.auth.service.AuthService;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입_성공() {
        // Given
        SignupRequestDto signupRequest = new SignupRequestDto("test@example.com", "Password1!@", "testuser", null);
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "email", "test@example.com");
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");

        Mockito.when(passwordEncoder.encode(any(String.class))).thenReturn("encryptedPassword");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            ReflectionTestUtils.setField(userArg, "id", 1L);
            return userArg;
        });

        // When
        SignupResponseDto actualResponse = authService.signup(signupRequest);

        // Then
        SignupResponseDto expectedResponse = new SignupResponseDto(1L, "test@example.com", "testuser", "회원가입이 완료되었습니다.");
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.getUsername(), actualResponse.getUsername());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    }


    @Test
    void 로그인_성공() {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto("test@example.com", "Password1!@");
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        ReflectionTestUtils.setField(dummyUser, "email", "test@example.com");
        ReflectionTestUtils.setField(dummyUser, "password", "encryptedPassword"); // 저장된 암호화된 비밀번호

        // PasswordEncoder.matches()가 true를 반환하도록 stub 처리
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // userRepository.findByEmail() 호출 시 dummyUser 반환
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(dummyUser));

        // jwtUtil.createToken() 호출 시 "mock-jwt-token" 반환하도록 stub 처리
        Mockito.when(jwtUtil.createToken(anyLong(), anyString())).thenReturn("mock-jwt-token");

        // When
        LoginResponseDto actualResponse = authService.login(loginRequest);

        // Then
        assertEquals("mock-jwt-token", actualResponse.getToken());
    }


    @Test
    void 로그아웃_성공() {
        // Given
        Long userId = 1L;
        String refreshToken = "dummy-refresh-token";

        // When
        authService.logout(userId, refreshToken);

    }

    @Test
    void 회원탈퇴_성공() {
        // Given
        Long userId = 1L;
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));

        // When
        authService.withdraw(userId);

        // Then: userRepository.delete()가 한 번 호출되었는지 검증
        Mockito.verify(userRepository, Mockito.times(1)).delete(dummyUser);
    }
}