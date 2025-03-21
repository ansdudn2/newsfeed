package com.example.newsfeed.domain.like.repository;

import com.example.newsfeed.domain.like.entity.Like;
import com.example.newsfeed.domain.user.entity.User;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 게시물에 좋아요를 눌렀는지 확인
    List<Like> findByUserAndFeed(User user, Feed feed);

    // 댓글에 좋아요를 눌렀는지 확인
    List<Like> findByUserAndComment(User user, Comment comment);
}
