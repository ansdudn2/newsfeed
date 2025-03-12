package com.example.newsfeed.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponseDto {

    private Long id;          // 사용자 ID

    private String email;     // 이메일

    private String username;  // 닉네임

    private String message;   // 응답 메시지
}

