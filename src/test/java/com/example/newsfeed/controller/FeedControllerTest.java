package com.example.newsfeed.controller;

import com.example.newsfeed.domain.feed.controller.FeedController;
import com.example.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.example.newsfeed.domain.feed.dto.request.FeedUpdateRequestDto;
import com.example.newsfeed.domain.feed.dto.response.FeedLikeCountResponseDto;
import com.example.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.service.FeedService;
import com.example.newsfeed.domain.follow.service.FollowService;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FeedControllerTest {

    @Mock
    private FollowService followService;

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    @Test
    void 게시물_작성_성공() {
        // Given
        FeedRequestDto createDto = new FeedRequestDto("제목입니다", "내용입니다");
        FeedResponseDto responseDto = new FeedResponseDto(
                1L,
                "제목입니다",
                "내용입니다",
                "testuser",
                LocalDateTime.parse("2025-03-14T01:00:00"),
                LocalDateTime.parse("2025-03-14T01:00:00")
        );
        Mockito.when(feedService.createFeed(
                any(User.class),
                any(FeedRequestDto.class)
        )).thenReturn(responseDto);

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<FeedResponseDto> response = feedController.createFeed(dummyUser, createDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("제목입니다", response.getBody().getTitle());
    }

    @Test
    void 전체_게시글_조회_성공() {
        // Given
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        FeedResponseDto dto1 = new FeedResponseDto(
                1L,
                "제목1",
                "내용1",
                "user1",
                LocalDateTime.parse("2025-03-14T01:00:00"),
                LocalDateTime.parse("2025-03-14T01:00:00")
        );
        FeedResponseDto dto2 = new FeedResponseDto(
                2L,
                "제목2",
                "내용2",
                "user2",
                LocalDateTime.parse("2025-03-14T02:00:00"),
                LocalDateTime.parse("2025-03-14T02:00:00")
        );

        Page<FeedResponseDto> feedPage = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        Mockito.when(feedService.getAllFeeds(eq(page))).thenReturn(feedPage);

        // When
        ResponseEntity<Page<FeedResponseDto>> response = feedController.getAllFeeds(page);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getTotalElements());
        assertEquals("제목1", response.getBody().getContent().get(0).getTitle());
        assertEquals("제목2", response.getBody().getContent().get(1).getTitle());
    }

    @Test
    void 게시물_단일조회_성공() {
        // Given
        FeedResponseDto responseDto = new FeedResponseDto(1L, "제목", "내용", "testuser", LocalDateTime.parse("2025-03-14T01:00:00"), LocalDateTime.parse("2025-03-14T01:00:00"));
        Mockito.when(feedService.getFeedById(eq(1L))).thenReturn(responseDto);

        // When
        ResponseEntity<FeedResponseDto> response = feedController.getFeedById(1L);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("제목", response.getBody().getTitle());
    }

    @Test
    void 게시물_수정_성공() {
        // Given
        Long feedId = 1L;
        FeedUpdateRequestDto updateDto = new FeedUpdateRequestDto("새제목", "새내용");
        FeedResponseDto responseDto = new FeedResponseDto(1L, "새제목", "새내용", "testuser", LocalDateTime.parse("2025-03-14T01:00:00"), LocalDateTime.parse("2025-03-14T01:00:00"));
        Mockito.when(feedService.updateFeed(Mockito.any(User.class), eq(feedId), Mockito.any(FeedUpdateRequestDto.class)))
                .thenReturn(responseDto);

        // 더미 User
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        // When
        ResponseEntity<FeedResponseDto> response = feedController.updateFeed(dummyUser, 1L, updateDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("새제목", response.getBody().getTitle());
    }

    @Test
    void 게시글_삭제_성공() {
        // Given
        Long feedId = 1L;
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        Mockito.doNothing().when(feedService).deleteFeed(dummyUser, feedId);

        // When
        ResponseEntity<?> response = feedController.deleteFeed(dummyUser, feedId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("게시글이 삭제되었습니다.", body.get("message"));
    }


    @Test
    void 팔로우한_사람들의_최신게시물_조회_성공() {
        // Given
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);

        Feed dummyFeed = new Feed();
        ReflectionTestUtils.setField(dummyFeed, "id", 1L);
        ReflectionTestUtils.setField(dummyFeed, "title", "제목입니다");
        ReflectionTestUtils.setField(dummyFeed, "content", "내용입니다");

        User feedUser = new User();
        ReflectionTestUtils.setField(feedUser, "username", "testuser");
        ReflectionTestUtils.setField(dummyFeed, "user", feedUser);
        ReflectionTestUtils.setField(dummyFeed, "createdAt", LocalDateTime.parse("2025-03-14T01:00:00"));
        ReflectionTestUtils.setField(dummyFeed, "updatedAt", LocalDateTime.parse("2025-03-14T01:00:00"));

        Page<Feed> feedPage = new PageImpl<>(Arrays.asList(dummyFeed), pageable, 1);

        Mockito.when(followService.getFeedsByFollowedUsers(eq(1L), Mockito.any(Pageable.class)))
                .thenReturn(feedPage);

        // When
        ResponseEntity<Page<FeedResponseDto>> response = feedController.getFeedsByFollowedUsers(dummyUser, pageable);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("제목입니다", response.getBody().getContent().get(0).getTitle());
        assertEquals("testuser", response.getBody().getContent().get(0).getUsername());
    }


    @Test
    void 수정일자_기준_최신순_정렬_테스트() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Feed> feedPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Mockito.when(feedService.getAllFeeds(Mockito.any(Pageable.class))).thenReturn(feedPage);

        // When
        Page<Feed> response = feedController.getFeedsSortedByUpdatedAt(pageable);

        // Then
        assertEquals(feedPage, response);
    }

    @Test
    void 좋아요_많은순_정렬_테스트() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");

        FeedLikeCountResponseDto dto = new FeedLikeCountResponseDto(
                1L,
                "제목입니다",
                "내용입니다",
                dummyUser,  // "testuser" 문자열 대신 dummyUser 객체 전달
                LocalDateTime.parse("2025-03-14T01:00:00"),
                LocalDateTime.parse("2025-03-14T01:00:00"),
                10L
        );
        Page<FeedLikeCountResponseDto> pageResponse = new PageImpl<>(Arrays.asList(dto), pageable, 1);

        Mockito.when(feedService.getFeedsSortedByLikes(Mockito.any(Pageable.class))).thenReturn(pageResponse);

        // When
        Page<FeedLikeCountResponseDto> response = feedController.getFeedsSortedByLikes(pageable);

        // Then
        assertEquals(1, response.getTotalElements());
        assertEquals(10L, response.getContent().get(0).getLikeCount());
    }

    @Test
    void 기간별_게시물_검색_테스트() {
        // Given
        LocalDateTime startDate = LocalDateTime.parse("2025-03-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2025-03-31T23:59:59");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Feed> feedPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Mockito.when(feedService.searchFeedsByDateRange(eq(startDate), eq(endDate), Mockito.any(Pageable.class)))
                .thenReturn(feedPage);

        // When
        Page<Feed> response = feedController.searchFeedsByDateRange(startDate, endDate, pageable);

        // Then
        assertEquals(feedPage, response);
    }
}
