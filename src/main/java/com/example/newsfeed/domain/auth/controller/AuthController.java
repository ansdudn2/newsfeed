package com.example.newsfeed.domain.auth.controller;

import com.example.newsfeed.domain.auth.dto.request.LoginRequestDto;
import com.example.newsfeed.domain.auth.dto.request.SignupRequestDto;
import com.example.newsfeed.domain.auth.dto.response.LoginResponseDto;
import com.example.newsfeed.domain.auth.dto.response.SignupResponseDto;
import com.example.newsfeed.domain.auth.service.AuthService;
import com.example.newsfeed.domain.common.annotation.Auth;
import com.example.newsfeed.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

   //회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Auth User user,
            @CookieValue(name = "REFRESH_TOKEN") String refreshToken) {
        authService.logout(user.getId(), refreshToken);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> withdraw(@Auth User user) {
        authService.withdraw(user.getId());
        return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
    }
}