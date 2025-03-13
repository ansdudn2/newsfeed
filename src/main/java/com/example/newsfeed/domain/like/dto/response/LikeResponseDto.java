package com.example.newsfeed.domain.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponseDto {
    private String message;  // 좋아요 상태 메시지
    private Long likeId;     // 좋아요 ID
}
