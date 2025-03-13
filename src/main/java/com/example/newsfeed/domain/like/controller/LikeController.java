package com.example.newsfeed.domain.like.controller;

import com.example.newsfeed.domain.like.dto.response.LikeResponseDto;
import com.example.newsfeed.domain.like.service.LikeService;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.common.annotation.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds/{feedId}")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 게시물 좋아요 추가/취소
    @PutMapping("/likes")
    public ResponseEntity<LikeResponseDto> likeFeed(@Auth User user, @PathVariable Long feedId) {
        String message = likeService.toggleFeedLike(user, feedId);
        return ResponseEntity.ok(new LikeResponseDto(message, feedId));
    }

    // 댓글 좋아요 추가/취소
    @PutMapping("/comments/{commentId}/likes")
    public ResponseEntity<LikeResponseDto> likeComment(@Auth User user, @PathVariable Long feedId, @PathVariable Long commentId) {
        // feedId가 댓글이 속한 게시물에 대한 값이므로, 이를 확인
        String message = likeService.toggleCommentLike(user, feedId, commentId);
        return ResponseEntity.ok(new LikeResponseDto(message, commentId));
    }
}


