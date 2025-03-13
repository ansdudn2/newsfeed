package com.example.newsfeed.domain.feed.repository;

import com.example.newsfeed.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    // 팔로우한 사람들의 게시물 조회
    Page<Feed> findByUserIdIn(List<Long> userIds, Pageable pageable);
}