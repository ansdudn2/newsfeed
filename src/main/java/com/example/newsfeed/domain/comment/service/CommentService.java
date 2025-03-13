package com.example.newsfeed.domain.comment.service;

import com.example.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.example.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.example.newsfeed.domain.comment.entity.Comment;
import com.example.newsfeed.domain.comment.repository.CommentRepository;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long feedId, User user, CommentRequestDto requestDto) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        Comment comment = new Comment();
        comment.setFeed(feed);
        comment.setUser(user);
        comment.setContent(requestDto.getContent());

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long feedId) {
        // 게시물이 존재하는지 확인
        feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        // 해당 게시물에 달린 댓글들을 조회
        List<Comment> comments = commentRepository.findByFeedId(feedId);
        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    //댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long feedId, Long commentId, User user, CommentRequestDto requestDto) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 댓글 작성자와 로그인한 사용자가 동일한지 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        // 댓글 내용 수정
        comment.setContent(requestDto.getContent());
        commentRepository.save(comment);  // 댓글 저장

        return new CommentResponseDto(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long feedId, Long commentId, User user) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 댓글 작성자와 로그인한 사용자가 동일한지 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.delete(comment);
    }
}
