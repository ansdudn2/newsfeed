package com.example.newsfeed.domain.auth.dto.response;

import com.example.newsfeed.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {

    private Long id;          // 사용자 ID

    private String email;     // 이메일

    private String username;  // 닉네임

    private String message;   // 응답 메시지

    //생성자
    public SignupResponseDto(User user, String message) {
        this.id = user.getId();           // id 값 추가
        this.email = user.getEmail();     // email 값 추가
        this.username = user.getUsername(); // username 값 추가
        this.message = message;           // 회원가입 메시지
    }
}

