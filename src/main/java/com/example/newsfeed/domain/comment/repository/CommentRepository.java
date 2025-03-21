package com.example.newsfeed.domain.comment.repository;

import com.example.newsfeed.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시물에 달린 모든 댓글을 조회
    List<Comment> findByFeedId(Long feedId);

    // 특정 게시물과 댓글 작성자에 대한 댓글 조회
    List<Comment> findByFeedIdAndUserId(Long feedId, Long userId);
}
