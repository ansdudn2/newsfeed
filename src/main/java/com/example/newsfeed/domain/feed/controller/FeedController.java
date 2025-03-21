package com.example.newsfeed.domain.feed.controller;

import com.example.newsfeed.domain.common.annotation.Auth;
import com.example.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.example.newsfeed.domain.feed.dto.request.FeedUpdateRequestDto;
import com.example.newsfeed.domain.feed.dto.response.FeedLikeCountResponseDto;
import com.example.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.service.FeedService;
import com.example.newsfeed.domain.follow.service.FollowService;
import com.example.newsfeed.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;


@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final FollowService followService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<FeedResponseDto> createFeed(@Auth User user,
                                                      @Valid @RequestBody FeedRequestDto requestDto) {
        return ResponseEntity.ok(feedService.createFeed(user, requestDto));
    }

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<Page<FeedResponseDto>> getAllFeeds(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(feedService.getAllFeeds(page));
    }

    // 단일 게시글 조회
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeedById(@PathVariable Long feedId) {
        return ResponseEntity.ok(feedService.getFeedById(feedId));
    }

    //게시글 수정
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> updateFeed(@Auth User user,
                                                      @PathVariable Long feedId,
                                                      @Valid @RequestBody FeedUpdateRequestDto requestDto) {
        return ResponseEntity.ok(feedService.updateFeed(user, feedId, requestDto));
    }

    //게시글 삭제
    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deleteFeed(@Auth User user, @PathVariable Long feedId) {
        feedService.deleteFeed(user, feedId);
        return ResponseEntity.ok(Collections.singletonMap("message", "게시글이 삭제되었습니다."));
    }

    // 팔로우한 사람들의 최신 게시물 조회
    @GetMapping("/followed")
    public ResponseEntity<Page<FeedResponseDto>> getFeedsByFollowedUsers(@Auth User user, Pageable pageable) {

        // 팔로우한 사람들의 게시물 조회
        Page<Feed> feeds = followService.getFeedsByFollowedUsers(user.getId(), pageable);

        // FeedResponseDto로 변환하여 반환
        Page<FeedResponseDto> response = feeds.map(feed -> new FeedResponseDto(feed));
        return ResponseEntity.ok(response);
    }

    // 수정일자 기준 최신순 정렬
    @GetMapping("/updatedAt")
    public Page<Feed> getFeedsSortedByUpdatedAt(Pageable pageable) {
        // 수정일자 기준 내림차순 정렬
        Pageable sortedByUpdatedAt = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("updatedAt")));
        return feedService.getAllFeeds(sortedByUpdatedAt);
    }

    // 좋아요 많은 순 정렬
    @GetMapping("/likes")
    public Page<FeedLikeCountResponseDto> getFeedsSortedByLikes(Pageable pageable) {
        return feedService.getFeedsSortedByLikes(pageable);
    }



    // 기간별 게시물 검색
    @GetMapping("/searchByDateRange")
    public Page<Feed> searchFeedsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return feedService.searchFeedsByDateRange(startDate, endDate, pageable);
    }
}

