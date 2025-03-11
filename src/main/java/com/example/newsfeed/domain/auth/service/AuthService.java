package com.example.newsfeed.domain.auth.service;

import com.example.newsfeed.config.JwtUtil;
import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.domain.auth.dto.request.LoginRequestDto;
import com.example.newsfeed.domain.auth.dto.request.SignupRequestDto;
import com.example.newsfeed.domain.auth.dto.response.LoginResponseDto;
import com.example.newsfeed.domain.auth.dto.response.SignupResponseDto;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //회원가입처리
    @Transactional
    public SignupResponseDto signup(SignupRequestDto request) {

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성 및 저장
        User user = new User(null, request.getEmail(), encodedPassword, request.getUsername());
        user = userRepository.save(user);

        return new SignupResponseDto(user.getId(), user.getEmail(), user.getUsername(), "회원가입이 완료되었습니다.");
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

}