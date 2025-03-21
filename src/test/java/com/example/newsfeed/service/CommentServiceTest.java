package com.example.newsfeed.service;

import com.example.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.example.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.example.newsfeed.domain.comment.entity.Comment;
import com.example.newsfeed.domain.comment.repository.CommentRepository;
import com.example.newsfeed.domain.comment.service.CommentService;
import com.example.newsfeed.domain.feed.entity.Feed;
import com.example.newsfeed.domain.feed.repository.FeedRepository;
import com.example.newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private FeedRepository feedRepository;

    @Test
    void 댓글_작성_성공() {
        // Given
        Long feedId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글 내용입니다");

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");

        Feed dummyFeed = new Feed();
        ReflectionTestUtils.setField(dummyFeed, "id", feedId);
        ReflectionTestUtils.setField(dummyFeed, "title", "게시글 제목");
        ReflectionTestUtils.setField(dummyFeed, "content", "게시글 내용");
        ReflectionTestUtils.setField(dummyFeed, "user", dummyUser);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(dummyFeed, "createdAt", now);
        ReflectionTestUtils.setField(dummyFeed, "updatedAt", now);

        Mockito.when(feedRepository.findById(eq(feedId))).thenReturn(Optional.of(dummyFeed));

        Mockito.when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        // When
        CommentResponseDto responseDto = commentService.createComment(feedId, dummyUser, requestDto);

        // Then
        assertEquals(1L, responseDto.getId());
        assertEquals("댓글 내용입니다", responseDto.getContent());
        assertEquals("testuser", responseDto.getUsername());
    }


    @Test
    void 댓글_조회_성공() {
        // Given
        Long feedId = 1L;
        Mockito.when(feedRepository.findById(feedId))
                .thenReturn(Optional.of(new Feed()));

        Comment comment1 = new Comment();
        ReflectionTestUtils.setField(comment1, "id", 1L);
        ReflectionTestUtils.setField(comment1, "content", "댓글1 내용");
        User user1 = new User();
        ReflectionTestUtils.setField(user1, "username", "user1");
        ReflectionTestUtils.setField(comment1, "user", user1);
        ReflectionTestUtils.setField(comment1, "createdAt", LocalDateTime.parse("2025-03-14T01:00:00"));
        ReflectionTestUtils.setField(comment1, "updatedAt", LocalDateTime.parse("2025-03-14T01:00:00"));

        Comment comment2 = new Comment();
        ReflectionTestUtils.setField(comment2, "id", 2L);
        ReflectionTestUtils.setField(comment2, "content", "댓글2 내용");
        User user2 = new User();
        ReflectionTestUtils.setField(user2, "username", "user2");
        ReflectionTestUtils.setField(comment2, "user", user2);
        ReflectionTestUtils.setField(comment2, "createdAt", LocalDateTime.parse("2025-03-14T02:00:00"));
        ReflectionTestUtils.setField(comment2, "updatedAt", LocalDateTime.parse("2025-03-14T02:00:00"));

        Mockito.when(commentRepository.findByFeedId(eq(feedId)))
                .thenReturn(Arrays.asList(comment1, comment2));

        // When
        List<CommentResponseDto> result = commentService.getComments(feedId);

        // Then
        assertEquals(2, result.size());
        assertEquals("댓글1 내용", result.get(0).getContent());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("댓글2 내용", result.get(1).getContent());
        assertEquals("user2", result.get(1).getUsername());
    }


    @Test
    void 댓글_수정_성공() {
        // given
        Long feedId = 1L;
        Long commentId = 1L;
        CommentRequestDto updateDto = new CommentRequestDto("수정된 댓글 내용");

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        ReflectionTestUtils.setField(dummyUser, "username", "testuser");

        Comment existingComment = new Comment();
        ReflectionTestUtils.setField(existingComment, "id", commentId);
        ReflectionTestUtils.setField(existingComment, "content", "원래 댓글 내용");
        ReflectionTestUtils.setField(existingComment, "user", dummyUser);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(existingComment, "createdAt", now);
        ReflectionTestUtils.setField(existingComment, "updatedAt", now);

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        Mockito.when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CommentResponseDto responseDto = commentService.updateComment(feedId, commentId, dummyUser, updateDto);
        // Then
        assertEquals("수정된 댓글 내용", responseDto.getContent());
    }

    @Test
    void 댓글_삭제_성공() {
        // Given
        Long feedId = 1L;
        Long commentId = 1L;

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        Comment comment = new Comment();
        ReflectionTestUtils.setField(comment, "id", commentId);
        ReflectionTestUtils.setField(comment, "user", dummyUser);

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(feedId, commentId, dummyUser);

        // Then
        Mockito.verify(commentRepository, Mockito.times(1)).delete(comment);
    }
}
