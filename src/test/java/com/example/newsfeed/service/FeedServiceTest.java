package com.example.newsfeed.service;

import com.example.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.example.newsfeed.domain.feed.dto.request.FeedUpdateRequestDto;
import com.example.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.feed.service.FeedService;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @InjectMocks
    private FeedService feedService;

    @Test
    void 게시글_작성_성공() {
        // Given
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");
        FeedRequestDto requestDto = new FeedRequestDto("제목입니다", "내용입니다");

        Feed feed = new Feed(requestDto.getTitle(), requestDto.getContent(), dummyUser);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(feed, "createdAt", now);
        ReflectionTestUtils.setField(feed, "updatedAt", now);

        Mockito.when(feedRepository.save(any(Feed.class))).thenAnswer(invocation -> {
            Feed savedFeed = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedFeed, "id", 1L);
            return savedFeed;
        });

        // When
        FeedResponseDto responseDto = feedService.createFeed(dummyUser, requestDto);

        // Then
        assertEquals(1L, responseDto.getId());
        assertEquals("제목입니다", responseDto.getTitle());
        assertEquals("내용입니다", responseDto.getContent());
    }


    @Test
    void 전체_게시글_조회_성공() {
        // Given
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Feed dummyFeed = new Feed("제목1", "내용1", new User());
        ReflectionTestUtils.setField(dummyFeed, "id", 1L);
        User feedUser = new User();
        ReflectionTestUtils.setField(feedUser, "username", "testuser");
        ReflectionTestUtils.setField(dummyFeed, "user", feedUser);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(dummyFeed, "createdAt", now);
        ReflectionTestUtils.setField(dummyFeed, "updatedAt", now);

        Page<Feed> feedPage = new PageImpl<>(Arrays.asList(dummyFeed), pageable, 1);
        Mockito.when(feedRepository.findAll(pageable)).thenReturn(feedPage);

        // When
        Page<FeedResponseDto> result = feedService.getAllFeeds(page);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("제목1", result.getContent().get(0).getTitle());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void 단일_게시글_조회_성공() {
        // Given
        Long feedId = 1L;
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");
        Feed feed = new Feed("제목", "내용", dummyUser);
        ReflectionTestUtils.setField(feed, "id", feedId);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(feed, "createdAt", now);
        ReflectionTestUtils.setField(feed, "updatedAt", now);
        Mockito.when(feedRepository.findById(feedId)).thenReturn(Optional.of(feed));

        // When
        FeedResponseDto responseDto = feedService.getFeedById(feedId);

        // Then
        assertEquals(feedId, responseDto.getId());
        assertEquals("제목", responseDto.getTitle());
        assertEquals("testuser", responseDto.getUsername());
    }

    @Test
    void 게시글_수정_성공() {
        // Given
        Long feedId = 1L;
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");

        FeedUpdateRequestDto updateDto = new FeedUpdateRequestDto("새제목", "새내용");
        Feed feed = new Feed("제목", "내용", dummyUser);
        ReflectionTestUtils.setField(feed, "id", feedId);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(feed, "createdAt", now);
        ReflectionTestUtils.setField(feed, "updatedAt", now);

        Mockito.when(feedRepository.findById(feedId)).thenReturn(Optional.of(feed));

        // When
        FeedResponseDto responseDto = feedService.updateFeed(dummyUser, feedId, updateDto);

        // Then
        assertEquals("새제목", responseDto.getTitle());
        assertEquals("새내용", responseDto.getContent());
    }


    @Test
    void 게시글_삭제_성공() {
        // Given
        Long feedId = 1L;
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        Feed feed = new Feed("제목", "내용", dummyUser);
        ReflectionTestUtils.setField(feed, "id", feedId);
        Mockito.when(feedRepository.findById(feedId)).thenReturn(Optional.of(feed));

        // When
        feedService.deleteFeed(dummyUser, feedId);

        // Then
        Mockito.verify(feedRepository, Mockito.times(1)).delete(feed);
    }

    @Test
    void 기간별_게시글_검색_성공() {
        // Given
        LocalDateTime startDate = LocalDateTime.parse("2025-03-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2025-03-31T23:59:59");
        Pageable pageable = PageRequest.of(0, 10);

        Feed feed = new Feed("제목", "내용", new User());
        ReflectionTestUtils.setField(feed, "id", 1L);
        ReflectionTestUtils.setField(feed, "createdAt", LocalDateTime.parse("2025-03-15T12:00:00"));
        Page<Feed> feedPage = new PageImpl<>(Arrays.asList(feed), pageable, 1);

        Mockito.when(feedRepository.findByCreatedAtBetween(eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(feedPage);

        // When
        Page<Feed> result = feedService.searchFeedsByDateRange(startDate, endDate, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("제목", result.getContent().get(0).getTitle());
    }
}