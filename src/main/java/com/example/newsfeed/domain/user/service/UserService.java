package com.example.newsfeed.domain.user.service;

import com.example.newsfeed.config.PasswordEncoder;
import com.example.newsfeed.domain.user.dto.request.UserUpdateRequestDto;
import com.example.newsfeed.domain.user.dto.response.UserProfileResponseDto;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 프로필 조회
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId) {
        // userId가 null일 경우 예외 처리
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 반드시 제공되어야 합니다.");
        }

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // UserProfileResponseDto로 변환하여 반환
        return new UserProfileResponseDto(user);
    }

    //프로필 수정
    @Transactional
    public void updateProfile(Long userId, UserUpdateRequestDto requestDto) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 반드시 제공되어야 합니다.");
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 닉네임과 자기소개 수정
        if (requestDto.getUsername() != null) {
            user.setUsername(requestDto.getUsername());
        }
        if (requestDto.getBio() != null) {
            user.setBio(requestDto.getBio());
        }

        // 비밀번호 수정이 요청되었을 때만 처리
        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty()) {
            // 현재 비밀번호 검증
            if (requestDto.getOldPassword() == null || requestDto.getOldPassword().isEmpty()) {
                throw new IllegalArgumentException("현재 비밀번호를 입력해야 합니다.");
            }

            // 기존 비밀번호가 맞지 않으면 예외
            if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
            }

            // 기존 비밀번호와 같으면 새 비밀번호로 변경 불가
            if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
                throw new IllegalArgumentException("새로운 비밀번호는 기존 비밀번호와 동일할 수 없습니다.");
            }

            // 새로운 비밀번호 암호화 후 저장
            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }

        // 변경된 사용자 정보 저장
        userRepository.save(user);
    }
}