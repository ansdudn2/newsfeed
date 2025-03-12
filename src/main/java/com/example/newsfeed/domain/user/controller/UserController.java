package com.example.newsfeed.domain.user.controller;

import com.example.newsfeed.domain.common.annotation.Auth;
import com.example.newsfeed.domain.user.dto.request.UserUpdateRequestDto;
import com.example.newsfeed.domain.user.dto.response.UserProfileResponseDto;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 프로필 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getProfile(@PathVariable Long userId) {
        UserProfileResponseDto response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    //프로필 수정
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@Auth User user,
                                           @Valid @RequestBody UserUpdateRequestDto requestDto) {
        userService.updateProfile(user, requestDto);
        return ResponseEntity.ok(Collections.singletonMap("message", "프로필이 수정되었습니다."));
    }
}
