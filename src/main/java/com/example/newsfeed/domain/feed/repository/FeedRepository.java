package com.example.newsfeed.domain.feed.repository;

import com.example.newsfeed.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

}


