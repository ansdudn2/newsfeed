package com.example.newsfeed.domain.like.entity;

import com.example.newsfeed.domain.comment.entity.Comment;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // 좋아요를 누른 사용자

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;  // 좋아요가 달린 게시물

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;  // 댓글에도 좋아요를 달 수 있도록

    public Like(User user, Feed feed, Comment comment) {
        if (feed == null && comment == null) {
            throw new IllegalArgumentException("게시물 또는 댓글을 좋아요할 수 있습니다.");
        }
        this.user = user;
        this.feed = feed;
        this.comment = comment;
    }
}

