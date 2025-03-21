package com.example.newsfeed.controller;

import com.example.newsfeed.domain.user.controller.UserController;
import com.example.newsfeed.domain.user.dto.request.UserUpdateRequestDto;
import com.example.newsfeed.domain.user.dto.response.UserProfileResponseDto;
import com.example.newsfeed.domain.user.service.UserService;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void 프로필_조회_성공() {
        // Given
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");
        ReflectionTestUtils.setField(dummyUser, "bio", "안녕하세요");
        LocalDateTime createdAt = LocalDateTime.parse("2025-03-14T01:00:00");
        LocalDateTime updatedAt = LocalDateTime.parse("2025-03-14T02:00:00");
        ReflectionTestUtils.setField(dummyUser, "createdAt", createdAt);
        ReflectionTestUtils.setField(dummyUser, "updatedAt", updatedAt);

        UserProfileResponseDto responseDto = new UserProfileResponseDto(dummyUser);

        Mockito.when(userService.getUserProfile(Mockito.eq(1L))).thenReturn(responseDto);

        // When
        ResponseEntity<UserProfileResponseDto> response = userController.getProfile(dummyUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("안녕하세요", response.getBody().getBio());
        assertEquals(createdAt, response.getBody().getCreatedAt());
        assertEquals(updatedAt, response.getBody().getUpdatedAt());
    }

    @Test
    void 프로필_수정_성공_유효성통과() {
        // Given
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto("newuser", "새로운 소개글", "oldPass1!@", "NewPass1!@");
        BindingResult bindingResult = new BeanPropertyBindingResult(updateDto, "userUpdateRequestDto");

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        Mockito.doNothing().when(userService).updateProfile(eq(1L), any(UserUpdateRequestDto.class));

        // When
        ResponseEntity<String> response = userController.updateProfile(dummyUser, updateDto, bindingResult);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("프로필이 수정되었습니다.", response.getBody());
    }

    @Test
    void 프로필_수정_실패_유효성오류() {
        // Given
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto("", "", "oldPass", "newPass");
        BindingResult bindingResult = new BeanPropertyBindingResult(updateDto, "userUpdateRequestDto");

        bindingResult.rejectValue("username", "NotBlank", "사용자 이름은 필수 입력 항목입니다.");

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<String> response = userController.updateProfile(dummyUser, updateDto, bindingResult);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("사용자 이름은 필수 입력 항목입니다."));
    }
}
