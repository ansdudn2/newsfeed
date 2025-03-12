package com.example.newsfeed.domain.feed.entity;

import com.example.newsfeed.domain.common.entity.BaseEntity;
import com.example.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feeds")
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 게시글 제목

    @Column(nullable = false, length = 1000)
    private String content; // 게시글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자 (User와 연관 관계)

    // 🔥 생성자 추가
    public Feed(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    // 🔥 수정 메서드 추가
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

