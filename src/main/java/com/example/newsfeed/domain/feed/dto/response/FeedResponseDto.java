package com.example.newsfeed.domain.feed.dto.response;

import com.example.newsfeed.domain.feed.entity.Feed;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FeedResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String username; // 작성자 닉네임
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public FeedResponseDto(Feed feed) {
        this.id = feed.getId();
        this.title = feed.getTitle();
        this.content = feed.getContent();
        this.username = feed.getUser().getUsername(); // User 엔티티에서 닉네임 가져옴
        this.createdAt = feed.getCreatedAt();
        this.updatedAt = feed.getUpdatedAt();
    }
}
