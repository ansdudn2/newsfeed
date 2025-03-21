package com.example.newsfeed.domain.follow.controller;

import com.example.newsfeed.domain.follow.service.FollowService;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.common.annotation.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 팔로우 추가 또는 취소
    @PostMapping("/{followingId}")
    public ResponseEntity<String> followUser(@Auth User user, @PathVariable Long followingId) {
        // 팔로우 추가/취소
        String message = followService.followUser(user.getId(), followingId);
        return ResponseEntity.ok(message);
    }
}