package com.example.newsfeed.domain.auth.service;

import com.example.newsfeed.config.JwtUtil;
import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.domain.auth.dto.request.LoginRequestDto;
import com.example.newsfeed.domain.auth.dto.request.SignupRequestDto;
import com.example.newsfeed.domain.auth.dto.response.LoginResponseDto;
import com.example.newsfeed.domain.auth.dto.response.SignupResponseDto;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //회원가입처리
    @Transactional
    public SignupResponseDto signup(@Valid SignupRequestDto request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = new User(request.getEmail(), encodedPassword, request.getUsername(), request.getBio());

        // 사용자 저장
        userRepository.save(user);

        // 회원가입 완료 메시지와 User 정보를 함께 반환
        return new SignupResponseDto(user, "회원가입이 완료되었습니다.");
    }

    //로그인 처리
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 이메일입니다.")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String token = jwtUtil.createToken(user.getId(), user.getEmail());

        return new LoginResponseDto(token);
    }

    //로그아웃관련 블랙
    private final Set<String> tokenBlacklist = Collections.synchronizedSet(new HashSet<>());

    public void logout(Long userId, String refreshToken) {
        tokenBlacklist.add(refreshToken);
    }

    @Transactional
    public void withdraw(Long userId) {
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 필요 시, 연관 데이터 처리 (게시글, 댓글, 좋아요, 팔로우 등 삭제 또는 soft delete)

        // 사용자 삭제
        userRepository.delete(user);
    }
}