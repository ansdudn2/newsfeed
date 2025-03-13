package com.example.newsfeed.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDto {
    private String message;  // 팔로우 상태 메시지 (팔로우 추가/취소 등)
}
