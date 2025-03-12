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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return new UserProfileResponseDto(user);
    }

    //프로필 수정
    @Transactional
    public void updateProfile(User user, UserUpdateRequestDto requestDto) {
        // 닉네임과 자기소개 변경
        if (requestDto.getUsername() != null) {
            user.setUsername(requestDto.getUsername());
        }
        if (requestDto.getBio() != null) {
            user.setBio(requestDto.getBio());
        }

        // 비밀번호 변경이 요청된 경우에만 검증
        if (StringUtils.hasText(requestDto.getNewPassword())) {
            if (!StringUtils.hasText(requestDto.getOldPassword())) {
                throw new IllegalArgumentException("현재 비밀번호를 입력해야 합니다.");
            }

            // 기존 비밀번호 검증
            if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
            }

            // 기존과 동일한 비밀번호로 변경 불가
            if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
                throw new IllegalArgumentException("새로운 비밀번호는 기존 비밀번호와 동일할 수 없습니다.");
            }

            // 새로운 비밀번호 저장 (암호화 후)
            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }

        userRepository.save(user);
    }
}
