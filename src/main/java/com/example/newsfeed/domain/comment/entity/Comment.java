package com.example.newsfeed.domain.comment.entity;

import com.example.newsfeed.domain.common.entity.BaseEntity;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed; // 댓글이 속한 게시물

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 댓글을 작성한 사용자

    @Column(nullable = false)
    private String content; // 댓글 내용
}

