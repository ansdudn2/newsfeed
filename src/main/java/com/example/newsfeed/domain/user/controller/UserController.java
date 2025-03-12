package com.example.newsfeed.domain.user.controller;

import com.example.newsfeed.domain.common.annotation.Auth;
import com.example.newsfeed.domain.user.dto.request.UserUpdateRequestDto;
import com.example.newsfeed.domain.user.dto.response.UserProfileResponseDto;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 프로필 조회 API
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getProfile(@Auth User user) {
        UserProfileResponseDto response = userService.getUserProfile(user.getId());
        return ResponseEntity.ok(response);
    }
    //프로필 수정
    @PutMapping("/me")
    public ResponseEntity<String> updateProfile(@Auth User user, @RequestBody @Valid UserUpdateRequestDto requestDto, BindingResult bindingResult) {

        // 유효성 검사 실패 시, 오류 메시지 반환
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append("\n");
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
        }

        // 로그인한 사용자 ID로 프로필 수정
        userService.updateProfile(user.getId(), requestDto);

        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }
}
