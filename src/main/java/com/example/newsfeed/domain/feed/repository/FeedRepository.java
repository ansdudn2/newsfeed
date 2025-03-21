package com.example.newsfeed.domain.feed.repository;

import com.example.newsfeed.domain.feed.dto.response.FeedLikeCountResponseDto;
import com.example.newsfeed.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    // 팔로우한 사람들의 게시물 조회
    Page<Feed> findByUserIdIn(List<Long> userIds, Pageable pageable);

    // 기간별 게시물 검색
    Page<Feed> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT new com.example.newsfeed.domain.feed.dto.response.FeedLikeCountResponseDto(" +
            "f.id, f.title, f.content, f.user, f.createdAt, f.updatedAt, COUNT(l)) " +
            "FROM Feed f LEFT JOIN com.example.newsfeed.domain.like.entity.Like l ON l.feed = f " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l) DESC")
    Page<FeedLikeCountResponseDto> findAllFeedsOrderByLikeCount(Pageable pageable);
}