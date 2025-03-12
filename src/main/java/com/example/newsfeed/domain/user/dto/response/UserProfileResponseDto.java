package com.example.newsfeed.domain.user.dto.response;

import com.example.newsfeed.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserProfileResponseDto {

    private Long id;

    private String username;

    private String bio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UserProfileResponseDto(User user) {

        this.id = user.getId();

        this.username = user.getUsername();

        this.bio = user.getBio();

        this.createdAt = user.getCreatedAt();

        this.updatedAt = user.getUpdatedAt();
    }
}

