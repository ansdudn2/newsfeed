package com.example.newsfeed.domain.follow.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDto {
    private Long followingId;  // 팔로우 받는 사용자 ID
}

